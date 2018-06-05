package com.stjerncraft.controlpanel.common.messages;

/**
 * Sent by the Client when first connecting, to verify whether the Client and Server version is compatible.
 * Server will reply with the same message, using its own version. 
 * Either the Client or Server will disconnect if it decides the other is incompatible.
 */
public class MessageVersion extends Message {
	public int versionMajor;
	public int versionMinor;
	public int versionFix;
	
	public MessageVersion() {}
	
	public MessageVersion(int versionMajor, int versionMinor, int versionFix) {
		this.versionMajor = versionMajor;
		this.versionMinor = versionMinor;
		this.versionFix = versionFix;
	}
	
	public boolean equals(MessageVersion other) {
		if(other == null)
			return false;
		return versionMajor == other.versionMajor && versionMinor == other.versionMinor && versionFix == other.versionFix;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof MessageVersion))
			return false;
		return equals((MessageVersion)obj);
	}
	
	@Override
	public String toString() {
		return versionMajor + "." + versionMinor + "." + versionFix;
	}
}

