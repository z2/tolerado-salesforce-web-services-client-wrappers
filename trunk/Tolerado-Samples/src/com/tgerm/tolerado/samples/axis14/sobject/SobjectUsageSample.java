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

package com.tgerm.tolerado.samples.axis14.sobject;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sforce.soap.partner.DeleteResult;
import com.sforce.soap.partner.QueryResult;
import com.sforce.soap.partner.SaveResult;
import com.sforce.soap.partner.sobject.SObject;
import com.tgerm.tolerado.axis14.core.ToleradoStubRegistry;
import com.tgerm.tolerado.axis14.core.method.WSRecoverableMethod;
import com.tgerm.tolerado.axis14.core.sobject.ToleradoSobject;
import com.tgerm.tolerado.axis14.core.util.Util;
import com.tgerm.tolerado.axis14.partner.ToleradoStub;
import com.tgerm.tolerado.common.Credential;
import com.tgerm.tolerado.common.ToleradoException;
import com.tgerm.tolerado.samples.cfg.LoginCfg;

public class SobjectUsageSample {
	private static Log log = LogFactory.getLog(SobjectUsageSample.class);

	public static void main(String[] args) {
		String sobjId = createSObject();
		querySObject(sobjId);
		deleteSobject(sobjId);
	}

	/**
	 * Shows how to delete Sobjects
	 * 
	 * @param sObjId
	 *            Sobject ids to delete
	 */
	static void deleteSobject(final String sObjId) {
		// Create a partner stub by giving your user name and password
		ToleradoStub partnerStub = ToleradoStubRegistry
				.getPartnerStub(new Credential("userName", "password"));
		//
		WSRecoverableMethod<DeleteResult[], ToleradoStub> deleteMethod = new WSRecoverableMethod<DeleteResult[], ToleradoStub>(
				"delete") {
			@Override
			protected DeleteResult[] invokeActual(ToleradoStub stub)
					throws Exception {
				final String[] idsToDelete = new String[] { sObjId };
				return stub.getPartnerBinding().delete(idsToDelete);
			}
		};

		DeleteResult[] results = deleteMethod.invoke(partnerStub);
	}

	private static String createSObject() {
		ToleradoStub partnerStub = ToleradoStubRegistry
				.getPartnerStub(LoginCfg.self.getCredential());
		ToleradoSobject sobj = new ToleradoSobject("Contact");
		sobj.setAttribute("FirstName", "Abhinav");
		sobj.setAttribute("LastName", "Gupta");
		log.debug("Created ToleradoSobject: " + sobj);
		// Get the updated Sobejct
		SObject updatedSObject = sobj.getUpdatedSObject();

		SObject[] sObjects = new SObject[] { updatedSObject };
		SaveResult[] saveResults = partnerStub.create(sObjects);
		// Throws Error in case Save is failed
		Util.checkSuccess(saveResults);
		return saveResults[0].getId();
	}

	private static void querySObject(String id) {
		ToleradoStub partnerStub = ToleradoStubRegistry
				.getPartnerStub(LoginCfg.self.getCredential());
		QueryResult qr = partnerStub
				.query("Select FirstName, LastName from Contact where id = '"
						+ id + "'");
		if (ArrayUtils.isEmpty(qr.getRecords())) {
			throw new ToleradoException("Failed to query the desired Sobject");
		}

		SObject sobj = qr.getRecords(0);
		ToleradoSobject tSobj = new ToleradoSobject(sobj);
		System.out.println("FirstName:" + tSobj.getTextValue("FirstName"));
		System.out.println("LastName:" + tSobj.getTextValue("LastName"));
	}
}
