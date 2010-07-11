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

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.tgerm.tolerado.axis14.apex.ToleradoApexStub;
import com.tgerm.tolerado.axis14.core.method.impl.LoginWSMethod;
import com.tgerm.tolerado.axis14.metadata.ToleradoMetaStub;
import com.tgerm.tolerado.axis14.partner.ToleradoStub;
import com.tgerm.tolerado.common.Credential;

/**
 * @author abhinav
 * 
 */
public class ToleradoStubRegistry {
	private static Log log = LogFactory.getLog(ToleradoStubRegistry.class);

	private static Map<Credential, ToleradoStub> cache = new HashMap<Credential, ToleradoStub>();

	/**
	 * Returns the correct stub for the given {@link ToleradoStub} class
	 * 
	 * @param credential
	 * @param stubClass
	 * @return
	 */
	public static ToleradoStub getStub(Credential credential,
			Class<? extends ToleradoStub> stubClass) {
		// Try using cache by default
		boolean forceLogin = false;
		return getStub(credential, forceLogin, stubClass);
	}

	public static ToleradoStub getStub(Credential credential,
			boolean forceLogin, Class<? extends ToleradoStub> stubClass) {
		if (forceLogin) {
			return renewStub(credential, stubClass);
		} else {
			return stubFor(credential, stubClass);
		}
	}

	public static ToleradoStub getPartnerStub(Credential cred) {
		return getApexStub(cred, false);
	}

	public static ToleradoStub getPartnerStub(Credential cred,
			boolean forceLogin) {
		return getStub(cred, forceLogin, ToleradoStub.class);
	}

	public static ToleradoApexStub getApexStub(Credential cred) {
		return getApexStub(cred, false);
	}

	public static ToleradoApexStub getApexStub(Credential cred,
			boolean forceLogin) {
		ToleradoApexStub stb = (ToleradoApexStub) getStub(cred, forceLogin,
				ToleradoApexStub.class);
		stb.prepareApex();
		return stb;
	}

	public static ToleradoMetaStub getMetaStub(Credential cred) {
		return getMetaStub(cred, false);
	}

	public static ToleradoMetaStub getMetaStub(Credential cred,
			boolean forceLogin) {
		ToleradoMetaStub stb = (ToleradoMetaStub) getStub(cred, forceLogin,
				ToleradoMetaStub.class);
		stb.prepareMeta();
		return stb;
	}

	private static ToleradoStub stubFor(Credential cred,
			Class<? extends ToleradoStub> stubClass) {
		ToleradoStub stub = cache.get(cred);
		if (stub == null) {
			synchronized (ToleradoStubRegistry.class) {
				if (stub == null) {
					stub = renewStub(cred, stubClass);
				}
			}
		}
		return stub;
	}

	private static ToleradoStub renewStub(Credential cred,
			Class<? extends ToleradoStub> stubClass) {
		log.debug("Login Call for " + cred.getUserName());
		LoginWSMethod l = new LoginWSMethod(cred, stubClass);
		ToleradoStub pStub = l.invoke(null);
		cache.put(cred, pStub);
		return pStub;
	}
}
