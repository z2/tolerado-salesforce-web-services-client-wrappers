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


/**
 * @author abhinav
 * 
 */
public class ToleradoSessionCache {
	private static Log log = LogFactory.getLog(ToleradoSessionCache.class);
	private static Map<Credential, ToleradoSession> cache = new HashMap<Credential, ToleradoSession>();
	// Create the correct login driver to use
	private static final LoginDriver LOGIN_DRIVER = LoginDriverLocator.locate();

	public static ToleradoSession sessionFor(Credential credential) {
		// Try using cache by default
		boolean forceLogin = false;
		return sessionFor(credential, forceLogin);
	}

	public static ToleradoSession sessionFor(Credential credential,
			boolean forceLogin) {
		if (forceLogin) {
			return renewSession(credential);
		} else {
			return sessionFromCache(credential);
		}
	}

	private static ToleradoSession sessionFromCache(Credential cred) {
		ToleradoSession stub = cache.get(cred);
		if (stub == null) {
			synchronized (ToleradoSessionCache.class) {
				if (stub == null) {
					stub = renewSession(cred);
				}
			}
		}
		return stub;
	}

	private static ToleradoSession renewSession(Credential cred) {
		log.debug("Login Call for " + cred.getUserName());
		ToleradoSession session = LOGIN_DRIVER.login(cred);
		cache.put(cred, session);
		return session;
	}
}
