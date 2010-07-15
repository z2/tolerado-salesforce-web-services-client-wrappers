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
 * A central registry/cache for {@link ToleradoStub}, {@link ToleradoMetaStub}
 * and {@link ToleradoApexStub}. This class gives methods like
 * {@link ToleradoStubRegistry#getPartnerStub(Credential)} to quickly get either
 * a cached or first time logged in Tolerado Stub impl.
 * 
 * In case one don't want cached stub and want to force login, then call this
 * one {@link ToleradoStubRegistry#getPartnerStub(Credential, boolean)} with
 * last boolean param as true
 * 
 * @author abhinav
 * 
 */
public class ToleradoStubRegistry {
	private static Log log = LogFactory.getLog(ToleradoStubRegistry.class);

	private static Map<Credential, ToleradoStub> cache = new HashMap<Credential, ToleradoStub>();

	/**
	 * Returns the correct stub for the given {@link ToleradoStub} class. Not
	 * really meant for public use, its used more internall by the API
	 * 
	 * @param credential
	 *            The Salesforce Login credentials
	 * @param stubClass
	 *            the {@link ToleradoStub} or its child class
	 * @return The ready to use {@link ToleradoStub} or its child class
	 *         polymorpically
	 */
	public static ToleradoStub getStub(Credential credential,
			Class<? extends ToleradoStub> stubClass) {
		// Try using cache by default
		boolean forceLogin = false;
		return getStub(credential, forceLogin, stubClass);
	}

	/**
	 * Returns the correct stub for the given {@link ToleradoStub} class. Not
	 * really meant for public use, its used more internall by the API
	 * 
	 * @param credential
	 *            The Salesforce Login credentials
	 * @param forceLogin
	 *            If true, login is enforced i.e. cached stub will no more be
	 *            returned
	 * @param stubClass
	 *            the {@link ToleradoStub} or its child class
	 * @return The ready to use {@link ToleradoStub} or its child class
	 *         polymorpically
	 */
	public static ToleradoStub getStub(Credential credential,
			boolean forceLogin, Class<? extends ToleradoStub> stubClass) {
		if (forceLogin) {
			return renewStub(credential, stubClass);
		} else {
			return stubFor(credential, stubClass);
		}
	}

	/**
	 * Returns the stub for Partner WSDL
	 * 
	 * @param credential
	 *            The Salesforce Login credentials
	 * @return The ready to use {@link ToleradoStub} for Partner WSDL
	 */
	public static ToleradoStub getPartnerStub(Credential cred) {
		return getApexStub(cred, false);
	}

	/**
	 * Returns the stub for Partner WSDL
	 * 
	 * @param credential
	 *            The Salesforce Login credentials
	 * @param forceLogin
	 *            If true, login is enforced i.e. cached stub will no more be
	 *            returned
	 * @return The ready to use {@link ToleradoStub} for Partner WSDL
	 */
	public static ToleradoStub getPartnerStub(Credential cred,
			boolean forceLogin) {
		return getStub(cred, forceLogin, ToleradoStub.class);
	}

	/**
	 * Returns the stub for Apex WSDL
	 * 
	 * @param credential
	 *            The Salesforce Login credentials
	 * @return The ready to use {@link ToleradoApexStub} for Apex WSDL
	 */
	public static ToleradoApexStub getApexStub(Credential cred) {
		return getApexStub(cred, false);
	}

	/**
	 * Returns the stub for Apex WSDL
	 * 
	 * @param credential
	 *            The Salesforce Login credentials
	 * @param forceLogin
	 *            If true, login is enforced i.e. cached stub will no more be
	 *            returned
	 * @return The ready to use {@link ToleradoApexStub} for Apex WSDL
	 */
	public static ToleradoApexStub getApexStub(Credential cred,
			boolean forceLogin) {
		ToleradoApexStub stb = (ToleradoApexStub) getStub(cred, forceLogin,
				ToleradoApexStub.class);
		stb.prepareApex();
		return stb;
	}

	/**
	 * Returns the stub for Metadata WSDL
	 * 
	 * @param credential
	 *            The Salesforce Login credentials
	 * @return The ready to use {@link ToleradoMetaStub} for Metadata WSDL
	 */
	public static ToleradoMetaStub getMetaStub(Credential cred) {
		return getMetaStub(cred, false);
	}

	/**
	 * Returns the stub for Metadata WSDL
	 * 
	 * @param credential
	 *            The Salesforce Login credentials
	 * @param forceLogin
	 *            If true, login is enforced i.e. cached stub will no more be
	 *            returned
	 * @return The ready to use {@link ToleradoMetaStub} for Metadata WSDL
	 */
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
