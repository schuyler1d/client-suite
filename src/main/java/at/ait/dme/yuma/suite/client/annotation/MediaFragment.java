package at.ait.dme.yuma.suite.client.annotation;

import java.io.Serializable;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * A base class for all types of media fragments.
 * 
 * @author Rainer Simon
 */
public abstract class MediaFragment implements Serializable, IsSerializable {

	private static final long serialVersionUID = -7282881259431812861L;

	public abstract boolean isVoid();
	
}
