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

package com.tgerm.tolerado.samples.axis14.stub.apex;

import com.sforce.soap._2006._08.apex.RunTestsResult;
import com.tgerm.tolerado.axis14.apex.ToleradoApexStub;
import com.tgerm.tolerado.axis14.core.ToleradoStubRegistry;
import com.tgerm.tolerado.samples.cfg.LoginCfg;

/**
 * Shows how to execute test cases using {@link ToleradoApexStub}
 * 
 * @author abhinav
 * 
 */
public class RunTestsSample {
	// Shows how to run all tests using the Apex WSDL ..
	public static void main(String[] args) {
		// Create a ToleradoApexStub
		ToleradoApexStub aStub = ToleradoStubRegistry.getApexStub(LoginCfg.self
				.getCredential());
		// This call does the rest
		RunTestsResult runResult = aStub.runAllTests();
		System.out.println("All Test Failures : " + runResult.getNumFailures());
	}

}
