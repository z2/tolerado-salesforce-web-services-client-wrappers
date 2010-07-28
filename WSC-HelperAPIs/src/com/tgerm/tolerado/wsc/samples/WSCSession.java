package com.tgerm.tolerado.wsc.samples;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.sforce.ws.ConnectionException;
import com.sforce.ws.ConnectorConfig;

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

public class WSCSession {
	public static final String CLS_PARTNER_CONNECTOR = "com.sforce.soap.partner.Connector";
	public static final String CLS_ENTERPRISE_CONNECTOR = "com.sforce.soap.enterprise.Connector";

	public static enum LoginWSDL {
		EnterpriseWSDL, PartnerWSDL
	}

	private final ConnectorConfig config;
	private String metadataServerUrl;
	private String serverUrl;
	private String sessionId;

	public WSCSession(LoginWSDL loginSource, String un, String pass)
			throws ConnectionException {
		config = new ConnectorConfig();
		config.setManualLogin(true);
		String connectorClassName = null;
		if (loginSource == LoginWSDL.EnterpriseWSDL) {
			connectorClassName = CLS_ENTERPRISE_CONNECTOR;
		} else {
			connectorClassName = CLS_PARTNER_CONNECTOR;
		}

		try {
			Class<?> connectorClass;
			connectorClass = Class.forName(connectorClassName);
			Method newConnectionMethod = connectorClass.getMethod(
					"newConnection", ConnectorConfig.class);
			Object connection = newConnectionMethod.invoke(null, config);
			Method loginMethod = connection.getClass().getMethod("login",
					String.class, String.class);
			Object loginResult = loginMethod.invoke(connection, un, pass);

			// Parses required information from login result
			parseLoginResult(loginResult);
		} catch (Exception e) {
			throw new RuntimeException(
					"Failed to Instantiate WSC Session Factory", e);
		}

	}

	private void parseLoginResult(Object loginResult)
			throws IntrospectionException, IllegalAccessException,
			InvocationTargetException {
		BeanInfo info = Introspector.getBeanInfo(loginResult.getClass());
		// All getMethods are called no args needed
		for (PropertyDescriptor pd : info.getPropertyDescriptors()) {
			String fieldName = pd.getName();
			if ("metadataServerUrl".equals(fieldName)) {
				metadataServerUrl = readPropertyValue(loginResult, pd);
			} else if ("serverUrl".equals(fieldName)) {
				serverUrl = readPropertyValue(loginResult, pd);
			} else if ("sessionId".equals(fieldName)) {
				sessionId = readPropertyValue(loginResult, pd);
			}
		}
	}

	private String readPropertyValue(Object loginResult, PropertyDescriptor pd)
			throws IllegalAccessException, InvocationTargetException {
		Object[] args = null;
		return (String) pd.getReadMethod().invoke(loginResult, args);
	}

	public String getSessionId() {
		return sessionId;
	}

	public String getMetadataServerUrl() {
		return metadataServerUrl;
	}

	public String getPartnerServerUrl() {
		return metadataServerUrl.replaceAll("/m/", "/u/");
	}

	public String getEnterpriseServerUrl() {
		return metadataServerUrl.replaceAll("/m/", "/c/");
	}

	public String getApexServerUrl() {
		return metadataServerUrl.replaceAll("/m/", "/s/");
	}

	@Override
	public String toString() {
		return "WSCSessionFactory [metadataServerUrl=" + metadataServerUrl
				+ ", serverUrl=" + serverUrl + ", sessionId=" + sessionId + "]";
	}
}
