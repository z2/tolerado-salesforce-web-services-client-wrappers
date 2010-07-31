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

package com.tgerm.tolerado.wsc.tests.partner;

import junit.framework.Assert;
import junit.framework.TestCase;

import com.sforce.soap.partner.QueryResult;
import com.sforce.soap.partner.SaveResult;
import com.sforce.soap.partner.sobject.SObject;
import com.tgerm.tolerado.samples.cfg.LoginCfg;
import com.tgerm.tolerado.wsc.core.Credential;
import com.tgerm.tolerado.wsc.partner.ToleradoPartnerStub;

public class PartnerWSDLTest extends TestCase {

	private Credential credential = LoginCfg.self.getCredential();

	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}

	public void testCreateContact() throws Exception {
		ToleradoPartnerStub stub = new ToleradoPartnerStub(credential);
		// Create a new Contact
		SObject contact = new SObject();
		contact.setType("Contact");
		String firstName = "Abhinav";
		// Current millis time added to avoid conflicts
		String lastName = "Gupta-" + System.currentTimeMillis();
		contact.setField("FirstName", firstName);
		contact.setField("LastName", lastName);
		SaveResult[] saveResults = stub.create(new SObject[] { contact });
		String savedContactId = null;
		for (SaveResult saveResult : saveResults) {
			if (!saveResult.isSuccess()) {
				Assert
						.fail("Failed to create Contact record using Partner for name :"
								+ firstName + " " + lastName);
			} else {
				savedContactId = saveResult.getId();
			}
		}

		QueryResult queryResult = stub
				.query("select Id, FirstName, LastName from Contact where Id ='"
						+ savedContactId + "'");
		Assert.assertNotNull(queryResult);
		Assert.assertNotNull(queryResult.getRecords());
		// Only 1 contact should come
		Assert.assertEquals(queryResult.getRecords().length, 1);
		SObject contactFetched = queryResult.getRecords()[0];
		Assert.assertEquals(contactFetched.getField("FirstName"), firstName);
		Assert.assertEquals(contactFetched.getField("LastName"), lastName);

	}
}
