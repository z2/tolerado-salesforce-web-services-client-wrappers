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

package com.tgerm.tolerado.axis14.core;

import com.tgerm.tolerado.axis14.core.ToleradoSession.SessionType;
import com.tgerm.tolerado.axis14.core.method.WSMethodErrorHandler;
import com.tgerm.tolerado.axis14.core.method.WSMethodErrorHandlerFactory;
import com.tgerm.tolerado.common.Credential;

/**
 * Base class for all Tolerado Stubs, it gives transparent salesforce session
 * caching. Also, gives api to renew session if required
 * 
 * @author abhinav
 * 
 */
public abstract class ToleradoStub {

	protected final Credential credential;
	protected ToleradoSession session;

	public ToleradoStub(Credential c) {
		this.credential = c;
		prepareSFDCSession(false);
	}

	public abstract void prepare();

	public void prepareSFDCSession(boolean forceNew) {
		session = ToleradoSessionCache.sessionFor(credential, forceNew);
		prepare();
	}

	public ToleradoSession getSession() {
		return session;
	}

	/**
	 * 
	 * @return The salesforce login {@link Credential} for this stub
	 */
	public Credential getCredential() {
		return credential;
	}

	public WSMethodErrorHandler getErrorHandler() {
		SessionType sessionType = session.getSessionType();
		return WSMethodErrorHandlerFactory.getErrorHandler(sessionType);
	}

}
