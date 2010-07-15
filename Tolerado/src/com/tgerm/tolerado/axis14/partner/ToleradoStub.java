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

package com.tgerm.tolerado.axis14.partner;

import com.sforce.soap.partner.DataCategoryGroupSobjectTypePair;
import com.sforce.soap.partner.DeleteResult;
import com.sforce.soap.partner.DescribeDataCategoryGroupResult;
import com.sforce.soap.partner.DescribeDataCategoryGroupStructureResult;
import com.sforce.soap.partner.DescribeGlobalResult;
import com.sforce.soap.partner.DescribeLayoutResult;
import com.sforce.soap.partner.DescribeSObjectResult;
import com.sforce.soap.partner.DescribeSoftphoneLayoutResult;
import com.sforce.soap.partner.DescribeTabSetResult;
import com.sforce.soap.partner.LeadConvert;
import com.sforce.soap.partner.LeadConvertResult;
import com.sforce.soap.partner.LoginResult;
import com.sforce.soap.partner.QueryResult;
import com.sforce.soap.partner.SaveResult;
import com.sforce.soap.partner.SessionHeader;
import com.sforce.soap.partner.SforceServiceLocator;
import com.sforce.soap.partner.SoapBindingStub;
import com.sforce.soap.partner.sobject.SObject;
import com.tgerm.tolerado.axis14.core.ToleradoStubRegistry;
import com.tgerm.tolerado.axis14.core.method.WSRecoverableMethod;
import com.tgerm.tolerado.common.Credential;

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
		return new WSRecoverableMethod<QueryResult, ToleradoStub>("Query") {
			@Override
			protected QueryResult invokeActual(ToleradoStub stub)
					throws Exception {
				QueryResult query = stub.getPartnerBinding().query(soql);
				return query;
			}
		}.invoke(this);
	}

	public QueryResult queryAll(final String soql) {
		return new WSRecoverableMethod<QueryResult, ToleradoStub>("QueryAll") {
			@Override
			protected QueryResult invokeActual(ToleradoStub stub)
					throws Exception {
				QueryResult query = stub.getPartnerBinding().queryAll(soql);
				return query;
			}
		}.invoke(this);
	}

	public QueryResult queryMore(final String queryLocator) {
		return new WSRecoverableMethod<QueryResult, ToleradoStub>("QueryMore") {
			@Override
			protected QueryResult invokeActual(ToleradoStub stub)
					throws Exception {
				QueryResult query = stub.getPartnerBinding().queryMore(
						queryLocator);
				return query;
			}
		}.invoke(this);
	}

	public LeadConvertResult[] convertLead(final LeadConvert[] leadConverts) {
		return new WSRecoverableMethod<LeadConvertResult[], ToleradoStub>(
				"convertLead") {
			@Override
			protected LeadConvertResult[] invokeActual(ToleradoStub stub)
					throws Exception {
				LeadConvertResult[] results = stub.getPartnerBinding()
						.convertLead(leadConverts);
				return results;
			}
		}.invoke(this);
	}

	public DeleteResult[] delete(final String[] ids) {
		return new WSRecoverableMethod<DeleteResult[], ToleradoStub>("delete") {
			@Override
			protected DeleteResult[] invokeActual(ToleradoStub stub)
					throws Exception {
				DeleteResult[] results = stub.getPartnerBinding().delete(ids);
				return results;
			}
		}.invoke(this);
	}

	public DescribeDataCategoryGroupResult[] describeDataCategoryGroups(
			final String[] sObjectType) {
		return new WSRecoverableMethod<DescribeDataCategoryGroupResult[], ToleradoStub>(
				"describeDataCategoryGroups") {
			@Override
			protected DescribeDataCategoryGroupResult[] invokeActual(
					ToleradoStub stub) throws Exception {
				DescribeDataCategoryGroupResult[] results = stub
						.getPartnerBinding().describeDataCategoryGroups(
								sObjectType);
				return results;
			}
		}.invoke(this);
	}

	public DescribeDataCategoryGroupStructureResult[] describeDataCategoryGroupStructures(
			final DataCategoryGroupSobjectTypePair[] pairs,
			final boolean topCategoriesOnly) {
		return new WSRecoverableMethod<DescribeDataCategoryGroupStructureResult[], ToleradoStub>(
				"describeDataCategoryGroupStructures") {
			@Override
			protected DescribeDataCategoryGroupStructureResult[] invokeActual(
					ToleradoStub stub) throws Exception {

				DescribeDataCategoryGroupStructureResult[] results = stub
						.getPartnerBinding()
						.describeDataCategoryGroupStructures(pairs,
								topCategoriesOnly);
				return results;
			}
		}.invoke(this);
	}

	public DescribeGlobalResult describeGlobal() {
		return new WSRecoverableMethod<DescribeGlobalResult, ToleradoStub>(
				"describeGlobal") {
			@Override
			protected DescribeGlobalResult invokeActual(ToleradoStub stub)
					throws Exception {
				DescribeGlobalResult results = stub.getPartnerBinding()
						.describeGlobal();
				return results;
			}
		}.invoke(this);
	}

	public DescribeLayoutResult describeLayout(final String sObjectType,
			final String[] recordTypeIds) {
		return new WSRecoverableMethod<DescribeLayoutResult, ToleradoStub>(
				"describeLayout") {
			@Override
			protected DescribeLayoutResult invokeActual(ToleradoStub stub)
					throws Exception {

				DescribeLayoutResult results = stub.getPartnerBinding()
						.describeLayout(sObjectType, recordTypeIds);
				return results;
			}
		}.invoke(this);
	}

	public DescribeSObjectResult describeSObject(final String sObjectType) {
		return new WSRecoverableMethod<DescribeSObjectResult, ToleradoStub>(
				"describeSObject") {
			@Override
			protected DescribeSObjectResult invokeActual(ToleradoStub stub)
					throws Exception {

				return stub.getPartnerBinding().describeSObject(sObjectType);
			}
		}.invoke(this);
	}

	public DescribeSObjectResult[] describeSObjects(final String[] sObjectType) {
		return new WSRecoverableMethod<DescribeSObjectResult[], ToleradoStub>(
				"describeSObjects") {
			@Override
			protected DescribeSObjectResult[] invokeActual(ToleradoStub stub)
					throws Exception {
				return stub.getPartnerBinding().describeSObjects(sObjectType);
			}
		}.invoke(this);
	}

	public DescribeSoftphoneLayoutResult describeSoftphoneLayout() {
		return new WSRecoverableMethod<DescribeSoftphoneLayoutResult, ToleradoStub>(
				"describeSoftphoneLayout") {
			@Override
			protected DescribeSoftphoneLayoutResult invokeActual(
					ToleradoStub stub) throws Exception {
				return stub.getPartnerBinding().describeSoftphoneLayout();
			}
		}.invoke(this);
	}

	public DescribeTabSetResult[] describeTabs() {
		return new WSRecoverableMethod<DescribeTabSetResult[], ToleradoStub>(
				"describeTabs") {
			@Override
			protected DescribeTabSetResult[] invokeActual(ToleradoStub stub)
					throws Exception {
				return stub.getPartnerBinding().describeTabs();
			}
		}.invoke(this);
	}

	public SaveResult[] create(final SObject[] sObjects) {
		return new WSRecoverableMethod<SaveResult[], ToleradoStub>("create") {
			@Override
			protected SaveResult[] invokeActual(ToleradoStub stub)
					throws Exception {
				return stub.getPartnerBinding().create(sObjects);
			}
		}.invoke(this);
	}

	public SaveResult[] update(final SObject[] sObjects) {
		return new WSRecoverableMethod<SaveResult[], ToleradoStub>("update") {
			@Override
			protected SaveResult[] invokeActual(ToleradoStub stub)
					throws Exception {
				return stub.getPartnerBinding().update(sObjects);
			}
		}.invoke(this);
	}
}
