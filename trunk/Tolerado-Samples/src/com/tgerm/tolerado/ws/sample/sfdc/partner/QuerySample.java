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
package com.tgerm.tolerado.ws.sample.sfdc.partner;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sforce.soap.partner.QueryResult;
import com.tgerm.tolerado.axis14.core.ToleradoStubRegistry;
import com.tgerm.tolerado.axis14.partner.ToleradoStub;
import com.tgerm.tolerado.cfg.LoginCfg;
import com.tgerm.tolerado.common.Credential;

/**
 * @author abhinav
 * 
 */
public class QuerySample {
	private static Log log = LogFactory.getLog(QuerySample.class);

	public static void main(String[] args) {
		Credential cred = LoginCfg.self.getCredential();
		// All the hassle of doing login and setting headers encapsulated in
		// this single call
		// ToleradoStub is a ready to use stub, with no changes required
		ToleradoStub pStub = ToleradoStubRegistry
				.getPartnerStub(new Credential("userName@user.com", "password"));
		// Binding created transparently from the given salesforce user name
		// password.
		// You transparently got the
		// 1. Fault recovery mechanism
		// 2. Cached stub (if its second call via the same login)
		// 3. QueryResult is same class as before, so no change on your rest of
		// the
		// logic.
		QueryResult qr = pStub.query("select FirstName, LastName from Contact");
		// ...
		// process the results within QueryResult
		// ...
	}
}
