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
package com.tgerm.tolerado.ws.sfdc.method;

import java.util.Arrays;
import java.util.List;

import org.apache.axis.AxisFault;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sforce.soap.partner.fault.ApiFault;
import com.sforce.soap.partner.fault.ExceptionCode;
import com.tgerm.tolerado.exception.ToleradoException;
import com.tgerm.tolerado.ws.sfdc.stub.ToleradoStub;
import com.tgerm.tolerado.ws.sfdc.util.Util;

/**
 * Instances of this class represents a single web service method. Those methods
 * can be from any WSDL like partner, apex, metadata or enterprise. An example
 * method can be "queryMore()" from partner wsdl.
 * 
 * This class gives a recoverable fixture to a web service method call, with
 * transparent healing by retrying a number of times for known and retryable
 * issues.
 * 
 * Child implementations can just extend this class and override the
 * {@link WSRecoverableMethod#invokeActual(ToleradoStub)} method to do the real web service
 * method call via the correct Soap Stub. For ex.
 * 
 * <pre>
 * 
 * Credential cred = new Credential(&quot;&lt;your login name&gt;&quot;, &quot;&lt;your pass&gt;&quot;);
 * ToleradoApexStub apexStub = ToleradoStubRegistry.getApexStub(cred);
 * 
 * RunTestsResult results = new WSMethod&lt;RunTestsResult, ToleradoApexStub&gt;(
 * 		&quot;runTests&quot;) {
 * 
 * 	&#064;Override
 * 	protected RunTestsResult invokeActual(ToleradoApexStub stub)
 * 			throws Exception {
 * 		// The real web service call done here using the Apex Binding 
 * 		// available in stub
 * 		return stub.getApexBinding().runTests(runTestsRequest);
 * 	}
 * }.invoke(apexStub);
 * 
 * </pre>
 * 
 * 
 * @author abhinav
 * 
 */
public abstract class WSRecoverableMethod<R, S extends ToleradoStub> {
	// this.getClass() used for accurate logging
	private Log log = LogFactory.getLog(this.getClass());

	protected int retries;

	private String methodName;

	public WSRecoverableMethod(String methodName) {
		super();
		this.methodName = methodName;
	}

	/**
	 * Method exposed to public client API
	 * 
	 * @param stub
	 *            The stub to be used internally by this call for accessing web
	 *            services.
	 * @return
	 * @throws ToleradoException
	 */
	public R invoke(S stub) {
		retries = 0;
		while (true) {
			try {
				return invokeActual(stub);
			} catch (Exception exc) {
				if (retries >= getMaxRetries()) {
					throw new ToleradoException(
							"All retry attempts failed to execute "
									+ getMethodInfo(), exc);
				}
				if (canRetry(exc)) {
					// Give a Retry
					retries++;
					log.warn("Retrying " + retries + " time, remote method : "
							+ getMethodInfo() + ", retrycount: " + retries
							+ ", ExceptionCode: " + exc.getMessage(), exc);

					// If Session is invalid, re login otherwise sleep.
					if (isLoginExpired(exc))
						// if login is required re-login other wise sleep
						reLogin(stub);
					else {
						waitBeforeNextRetry();
					}
				} else {
					// All retries exhausted, fail now
					throw new ToleradoException(
							"Unknown Exception occured for " + getMethodInfo(),
							exc);
				}
			}
		}

	}

	/**
	 * 
	 * @return Returns some human readable information about this webservice
	 *         call impl
	 */
	protected String getMethodInfo() {
		return methodName;
	}

	/**
	 * This method will do the real call, meant to be retried by invoke(S)
	 * 
	 * @param stub
	 *            Stub to be used for any operation
	 * @return
	 * @throws Exception
	 */
	protected abstract R invokeActual(S stub) throws Exception;

	/**
	 * Will be called on Retryable failure
	 */
	protected void waitBeforeNextRetry() {
		try {
			int delta = 1 + retries;
			Thread.sleep(3000 * delta);
		} catch (InterruptedException e) {
		}
	}

	/**
	 * Returns true if the Exception is coming because of Login expiration.
	 * 
	 * @param ex
	 * @return
	 */
	protected boolean isLoginExpired(Exception t) {
		if (t instanceof ApiFault) {
			ExceptionCode code = null;
			ApiFault af = (ApiFault) t;
			code = af.getExceptionCode();
			return code != null
					&& ExceptionCode.INVALID_SESSION_ID.equals(code);
		} else if (t instanceof AxisFault) {
			AxisFault af = (AxisFault) t;
			String exCode = Util.faultCodeFromAxisFault(af);
			if (exCode != null) {
				return exCode.equalsIgnoreCase("INVALID_SESSION_ID");
			}
		}
		return false;

	}

	/**
	 * Renews the user's session, if login was previously expired
	 * 
	 * @param stub
	 */
	protected void reLogin(S stub) {
		if (stub != null)
			stub.renewSession();
	}

	/**
	 * Child implementations can override if more retries are needed. It
	 * defaults to 5.
	 * 
	 * @return Returns maximum retries that should be made
	 */
	protected int getMaxRetries() {
		return 5;
	}

	/**
	 * Returns true if this Exception can be retried
	 * 
	 * @param ex
	 * @return
	 */
	protected boolean canRetry(Exception t) {
		if (t instanceof ApiFault) {
			ApiFault af = (ApiFault) t;
			ExceptionCode code = af.getExceptionCode();
			log.debug("code:" + code);
			if (code != null)
				return RETRYABLES.contains(code.getValue().toLowerCase());
		} else if (t instanceof AxisFault) {
			AxisFault af = (AxisFault) t;
			if (af.getCause() != null
					&& af.getCause() instanceof java.net.SocketException) {
				return true;
			}

			String faultString = af.getFaultString();
			if (faultString != null
					&& faultString.indexOf("UnknownHostException") != -1) {
				return true;
			}

			String exCode = Util.faultCodeFromAxisFault(af);
			log.debug("code:" + exCode);
			if (exCode != null) {
				return RETRYABLES.contains(exCode.toLowerCase());
			}

		}
		return false;

	}

	/**
	 * List of Retryable exceptions
	 */
	private static List<String> RETRYABLES = Arrays.asList(new String[] {
			ExceptionCode.CLIENT_NOT_ACCESSIBLE_FOR_USER.getValue()
					.toLowerCase(),
			ExceptionCode.CLIENT_REQUIRE_UPDATE_FOR_USER.getValue()
					.toLowerCase(),
			ExceptionCode.INVALID_SESSION_ID.getValue().toLowerCase(),
			ExceptionCode.QUERY_TIMEOUT.getValue().toLowerCase(),
			ExceptionCode.REQUEST_RUNNING_TOO_LONG.getValue().toLowerCase(),
			ExceptionCode.SERVER_UNAVAILABLE.getValue().toLowerCase(),
			ExceptionCode.SSO_SERVICE_DOWN.getValue().toLowerCase(),
			ExceptionCode.UNKNOWN_EXCEPTION.getValue().toLowerCase() });

}
