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
package com.tgerm.tolerado.samples.axis14.stub.partner;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sforce.soap.partner.sobject.SObject;
import com.tgerm.tolerado.axis14.core.ToleradoStubRegistry;
import com.tgerm.tolerado.axis14.partner.ToleradoQuery;
import com.tgerm.tolerado.axis14.partner.ToleradoStub;
import com.tgerm.tolerado.common.Credential;
import com.tgerm.tolerado.samples.cfg.LoginCfg;

/**
 * @author abhinav
 * 
 */
public class QueryMoreSample {
	private static Log log = LogFactory.getLog(QueryMoreSample.class);

	public static void main(String[] args) throws Exception {
		query(LoginCfg.self.getCredential());
	}

	private static void query(Credential cred) throws Exception {
		ToleradoStub pStub = ToleradoStubRegistry.getPartnerStub(cred);

		ToleradoQuery q = new ToleradoQuery(pStub, "Select Name from Contact",
				400);
		while (q.hasMoreRecords()) {
			SObject[] records = q.getRecords();
			log.debug("Fetched next " + records.length + " records !");
		}
	}

}
