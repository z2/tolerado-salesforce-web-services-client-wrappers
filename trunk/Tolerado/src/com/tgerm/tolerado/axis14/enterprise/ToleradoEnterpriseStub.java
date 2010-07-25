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

import javax.xml.rpc.ServiceException;

import com.sforce.soap.enterprise.QueryResult;
import com.sforce.soap.enterprise.SessionHeader;
import com.sforce.soap.enterprise.SforceServiceLocator;
import com.sforce.soap.enterprise.SoapBindingStub;
import com.tgerm.tolerado.axis14.core.ToleradoStub;
import com.tgerm.tolerado.axis14.core.method.WSRecoverableMethod;
import com.tgerm.tolerado.common.Credential;
import com.tgerm.tolerado.common.ToleradoException;

/**
 * {@link ToleradoEnterpriseStub} for Enterprise WSDL
 * 
 * @author abhinav
 * 
 */
public class ToleradoEnterpriseStub extends ToleradoStub {
	private SoapBindingStub binding;

	public ToleradoEnterpriseStub(Credential cred) {
		super(cred);
	}

	/**
	 * @return The {@link SoapBindingStub} for partner wsdl
	 */
	public SoapBindingStub getEnterpriseBinding() {
		return binding;
	}

	@Override
	public void prepare() {
		try {
			binding = (SoapBindingStub) new SforceServiceLocator().getSoap();
		} catch (ServiceException e) {
			throw new ToleradoException("Failed to create SoapBindingStub", e);
		}
		String serverUrl = session.getServerUrl().replaceFirst("/u/", "/c/");
		binding._setProperty(SoapBindingStub.ENDPOINT_ADDRESS_PROPERTY,
				serverUrl);
		SessionHeader sh = new SessionHeader();
		sh.setSessionId(session.getSessionId());
		binding.setHeader(new SforceServiceLocator().getServiceName()
				.getNamespaceURI(), "SessionHeader", sh);
		binding.setTimeout(60000);
	}

	/**
	 * Queries salesforce via SOQL
	 * 
	 * @param soql
	 * @return
	 */
	public QueryResult query(final String soql) {
		WSRecoverableMethod<QueryResult, ToleradoEnterpriseStub> wsMethod = new WSRecoverableMethod<QueryResult, ToleradoEnterpriseStub>(
				"Query") {
			@Override
			protected QueryResult invokeActual(ToleradoEnterpriseStub stub)
					throws Exception {
				QueryResult query = stub.getEnterpriseBinding().query(soql);
				return query;
			}
		};
		return wsMethod.invoke(this);
	}

	public QueryResult queryAll(final String soql) {
		return new WSRecoverableMethod<QueryResult, ToleradoEnterpriseStub>(
				"QueryAll") {
			@Override
			protected QueryResult invokeActual(ToleradoEnterpriseStub stub)
					throws Exception {
				QueryResult query = stub.getEnterpriseBinding().queryAll(soql);
				return query;
			}
		}.invoke(this);
	}

	public QueryResult queryMore(final String queryLocator) {
		return new WSRecoverableMethod<QueryResult, ToleradoEnterpriseStub>(
				"QueryMore") {
			@Override
			protected QueryResult invokeActual(ToleradoEnterpriseStub stub)
					throws Exception {
				QueryResult query = stub.getEnterpriseBinding().queryMore(
						queryLocator);
				return query;
			}
		}.invoke(this);
	}
}
