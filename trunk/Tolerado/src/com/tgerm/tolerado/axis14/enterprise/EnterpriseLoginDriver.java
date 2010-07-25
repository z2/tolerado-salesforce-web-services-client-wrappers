/*
Copyright (c) 2010 tgerm.com
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions
are met:

1. Redistributions of source code must retain the above copyright
   notice, this list of conditions and the following disclaimer.
2. Redistributions in binary form must reproduce the above copyright
   notice, this list of conditions and the following disclaimer in the
   documentation and/or other materials provided with the distribution.
3. The name of the author may not be used to endorse or promote products
   derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE AUTHOR "AS IS" AND ANY EXPRESS OR
IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT, 
INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.tgerm.tolerado.axis14.enterprise;

import com.sforce.soap.enterprise.LoginResult;

import com.tgerm.tolerado.axis14.core.LoginDriver;
import com.tgerm.tolerado.axis14.core.ToleradoSession;
import com.tgerm.tolerado.common.Credential;

/**
 * Login driver that uses Enterprise WSDL to create session
 * 
 * @author abhinav
 * 
 */
public class EnterpriseLoginDriver implements LoginDriver {

	@Override
	public ToleradoSession login(Credential cred) {
		EnterpriseLoginWSMethod l = new EnterpriseLoginWSMethod(cred);
		final LoginResult lres = l.invoke(null);
		return new ToleradoSession() {

			@Override
			public String getSessionId() {
				return lres.getSessionId();
			}

			@Override
			public String getServerUrl() {
				return lres.getServerUrl();
			}

			@Override
			public Object getLoginResult() {
				return lres;
			}

			@Override
			public String getMetadataServerUrl() {
				return lres.getMetadataServerUrl();
			}

			@Override
			public SessionType getSessionType() {
				return SessionType.Enterprise;
			}
		};
	}
}
