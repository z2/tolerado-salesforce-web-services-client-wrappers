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

package com.tgerm.tolerado.ws.sfdc.stub;

import com.sforce.soap.partner.LoginResult;
import com.sforce.soap.partner.QueryResult;
import com.sforce.soap.partner.SaveResult;
import com.sforce.soap.partner.SessionHeader;
import com.sforce.soap.partner.SforceServiceLocator;
import com.sforce.soap.partner.SoapBindingStub;
import com.sforce.soap.partner.sobject.SObject;
import com.tgerm.tolerado.ws.sfdc.Credential;
import com.tgerm.tolerado.ws.sfdc.method.WSMethod;

/**
 * @author abhinav
 * 
 */
public class ToleradoStub {
	private LoginResult loginResult;
	private SoapBindingStub partnerBinding;
	private Credential credential;

	public ToleradoStub() {

	}

	public LoginResult getLoginResult() {
		return loginResult;
	}

	public void setLoginResult(LoginResult loginResult) {
		this.loginResult = loginResult;
	}

	public SoapBindingStub getPartnerBinding() {
		return partnerBinding;
	}

	public void setPartnerBinding(SoapBindingStub binding) {
		this.partnerBinding = binding;
	}

	public Credential getCredential() {
		return credential;
	}

	public void setCredential(Credential credential) {
		this.credential = credential;
	}

	public void prepare() {
		partnerBinding._setProperty(SoapBindingStub.ENDPOINT_ADDRESS_PROPERTY,
				loginResult.getServerUrl());
		SessionHeader sh = new SessionHeader();
		sh.setSessionId(loginResult.getSessionId());
		partnerBinding.setHeader(new SforceServiceLocator().getServiceName()
				.getNamespaceURI(), "SessionHeader", sh);
		partnerBinding.setTimeout(60000);
	}

	/**
	 * Can be invoked by client to relogin and get a new session id
	 */
	public void renewSession() {
		boolean forceLogin = true;
		ToleradoStub newStub = ToleradoStubRegistry.getStub(getCredential(),
				forceLogin, this.getClass());
		this.partnerBinding = newStub.partnerBinding;
		this.loginResult = newStub.loginResult;
	}

	public QueryResult query(final String soql) {
		return new WSMethod<QueryResult, ToleradoStub>("Query") {
			@Override
			protected QueryResult invokeActual(ToleradoStub stub)
					throws Exception {
				QueryResult query = stub.getPartnerBinding().query(soql);
				return query;
			}
		}.invoke(this);
	}

	public QueryResult queryAll(final String soql) {
		return new WSMethod<QueryResult, ToleradoStub>("QueryAll") {
			@Override
			protected QueryResult invokeActual(ToleradoStub stub)
					throws Exception {
				QueryResult query = stub.getPartnerBinding().queryAll(soql);
				return query;
			}
		}.invoke(this);
	}

	public QueryResult queryMore(final String queryLocator) {
		return new WSMethod<QueryResult, ToleradoStub>("QueryMore") {
			@Override
			protected QueryResult invokeActual(ToleradoStub stub)
					throws Exception {
				QueryResult query = stub.getPartnerBinding().queryMore(
						queryLocator);
				return query;
			}
		}.invoke(this);
	}

	public SaveResult[] create(final SObject[] sObjects) {
		return new WSMethod<SaveResult[], ToleradoStub>("create") {
			@Override
			protected SaveResult[] invokeActual(ToleradoStub stub)
					throws Exception {
				return stub.getPartnerBinding().create(sObjects);
			}
		}.invoke(this);
	}

	public SaveResult[] update(final SObject[] sObjects) {
		return new WSMethod<SaveResult[], ToleradoStub>("update") {
			@Override
			protected SaveResult[] invokeActual(ToleradoStub stub)
					throws Exception {
				return stub.getPartnerBinding().update(sObjects);
			}
		}.invoke(this);
	}
}
