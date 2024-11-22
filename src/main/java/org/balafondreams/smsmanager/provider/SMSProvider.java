package org.balafondreams.smsmanager.provider;

public interface SMSProvider {
    SMSResponse sendMessage(SMSRequest request);
}
