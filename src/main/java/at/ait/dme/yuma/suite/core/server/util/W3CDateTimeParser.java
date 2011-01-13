/*
 * Copyright 2008-2010 Austrian Institute of Technology
 *
 * Licensed under the EUPL, Version 1.1 or - as soon they
 * will be approved by the European Commission - subsequent
 * versions of the EUPL (the "Licence");
 * you may not use this work except in compliance with the
 * Licence.
 * You may obtain a copy of the Licence at:
 *
 * http://ec.europa.eu/idabc/eupl
 *
 * Unless required by applicable law or agreed to in
 * writing, software distributed under the Licence is
 * distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied.
 * See the Licence for the specific language governing
 * permissions and limitations under the Licence.
 */

package at.ait.dme.yuma.suite.core.server.util;

import java.text.DateFormat;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class W3CDateTimeParser {
    private static final String[] W3CDATETIME_MASKS = {
        "yyyy-MM-dd'T'HH:mm:ss.SSSz",
        "yyyy-MM-dd't'HH:mm:ss.SSSz",
        "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
        "yyyy-MM-dd't'HH:mm:ss.SSS'z'",
        "yyyy-MM-dd'T'HH:mm:ssz",
        "yyyy-MM-dd't'HH:mm:ssz",
        "yyyy-MM-dd'T'HH:mm:ss'Z'",
        "yyyy-MM-dd't'HH:mm:ss'z'",
        "yyyy-MM-dd'T'HH:mmz",   
        "yyyy-MM'T'HH:mmz",      
        "yyyy'T'HH:mmz",          
        "yyyy-MM-dd't'HH:mmz", 
        "yyyy-MM-dd'T'HH:mm'Z'", 
        "yyyy-MM-dd't'HH:mm'z'", 
        "yyyy-MM-dd",
        "yyyy-MM",
        "yyyy"
    };

    public static String formatW3CDateTime(Date date) {
    	SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
    	String sDate = dateFormat.format(date);
    	
    	TimeZone tz=Calendar.getInstance().getTimeZone();
    	long tzOffset = tz.getRawOffset() + ((tz.inDaylightTime(date))?tz.getDSTSavings():0);
    	long hours = tzOffset / (1000*60*60);
    	long minutes = (tzOffset % (1000*60*60)) / (1000*60);
    	return sDate+=String.format("%+03d:%02d", hours, minutes);
    }
    
    public static Date parseW3CDateTime(String sDate) {
        int tIndex = sDate.indexOf("T");
        if (tIndex>-1) {
            if (sDate.endsWith("Z")) {
                sDate = sDate.substring(0,sDate.length()-1)+"+00:00";
            }
            int tzdIndex = sDate.indexOf("+",tIndex);
            if (tzdIndex==-1) {
                tzdIndex = sDate.indexOf("-",tIndex);
            }
            if (tzdIndex>-1) {
                String pre = sDate.substring(0,tzdIndex);
                int secFraction = pre.indexOf(",");
                if (secFraction>-1) {
                    pre = pre.substring(0,secFraction);
                }
                String post = sDate.substring(tzdIndex);
                sDate = pre + "GMT" + post;
            }
        }
        else {
            sDate += "T00:00GMT";
        }
        return parseUsingMask(W3CDATETIME_MASKS,sDate);
    }

    private static Date parseUsingMask(String[] masks,String sDate) {
        sDate = (sDate!=null) ? sDate.trim() : null;
        ParsePosition pp = null;
        Date d = null;
        for (int i=0;d==null && i<masks.length;i++) {
            DateFormat df = new SimpleDateFormat(masks[i]);
            df.setLenient(true);
            pp = new ParsePosition(0);
            d = df.parse(sDate,pp);
            if (pp.getIndex()!=sDate.length()) {
                    d = null;
            }            
        }
        return d;
    }
}
