/*******************************************************************************
 * Copyright 2015 Enkive, LLC.
 *
 * This file is part of Enkive CE (Community Edition).
 *
 * Enkive CE is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of
 * the License, or (at your option) any later version.
 *
 * Enkive CE is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public
 * License along with Enkive CE. If not, see
 * <http://www.gnu.org/licenses/>.
 *******************************************************************************/
package com.linuxbox.enkive.message;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.james.mime4j.MimeException;
import org.apache.james.mime4j.MimeIOException;
import org.apache.james.mime4j.stream.EntityState;
import org.apache.james.mime4j.stream.MimeConfig;
import org.apache.james.mime4j.stream.RecursionMode;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.Stack;


public class EnkiveMessageParser {
	private final static Log LOGGER = LogFactory
			.getLog("com.linuxbox.enkive.message");

	private MimeConfig config;

	public EnkiveMessageParser() {
		// Set a config that is as lenient as possible
		// We want to parse all messages that come through,
		// not throw out any that do not adhere to standards
		config = new MimeConfig();
		config.setStrictParsing(false);
		config.setMaxLineLen(-1);
		config.setMaxContentLen(-1);
		config.setMaxHeaderCount(-1);
		config.setMaxHeaderLen(-1);
		config.setMalformedHeaderStartsBody(true);

	}

	/**
	 * @throws IOException
	 * @throws MimeIOException
	 * @throws MimeException
	 *
	 * @param in
	 *            in An InputStream of the message to be parsed
	 * 
	 *            Constructs a com.linuxbox.enkive.message object from a raw
	 *            email message InputStream
	 */
	public EnkiveMessage ConstructMessage(InputStream in) {

		try {

			Stack<EnkiveMessagePartMulti> headerStack = new Stack<EnkiveMessagePartMulti>();
			EnkiveMessagePartMulti mp;
			StringBuilder headers = new StringBuilder();
			boolean messageHeadersParsed = false;
			boolean isMultiPart = false;

			EnkiveMessage enkiveMessage = new EnkiveMessage();

			// TODO Get line ending from message
			final String lineEnding = IOUtils.LINE_SEPARATOR;
			enkiveMessage.setLineSeparator(lineEnding);


			final MessageStreamParser stream = new MessageStreamParser(config);
			stream.setRecursionMode(RecursionMode.M_NO_RECURSE);

			stream.parse(in);

			for (EntityState state = stream.getState(); state != EntityState.T_END_OF_STREAM; state = stream
					.next()) {
				switch (state) {
					// At the start of a header section we want to reset the local
					// header variable since we only want to store the headers
					// for the section currently being parsed
					case T_START_HEADER:
						headers = new StringBuilder();
						break;

					// Append each header field to the local header variable
					case T_FIELD:
						headers.append(stream.getField());
						headers.append(lineEnding);
						break;

					// If we haven't set the message headers set them and
					// clear the variable so they don't get stored in a
					// ContentHeader object
					case T_END_HEADER:
						if (!messageHeadersParsed) {
							enkiveMessage.setOriginalHeaders(headers.toString());
							messageHeadersParsed = true;
							headers = new StringBuilder();
						}
						break;

					// If we have a multipart message, create a new object,
					// grab the information we need and push it on the stack
					case T_START_MULTIPART:
						isMultiPart = true;
						mp = new EnkiveMessagePartMulti();
						mp.setBoundary(stream.getBodyDescriptor().getBoundary());
						mp.setOriginalHeaders(headers.toString());
						mp.setLineEnding(lineEnding);
						headerStack.push(mp);
						break;

					// If there's a preamble, get the multipartheader off
					// the top of the stack, set it, and push back on the stack
					case T_PREAMBLE:

						BufferedReader reader = new BufferedReader(
								stream.getReader());

						String tempString;
						String preamble = "";
						while ((tempString = reader.readLine()) != null) {
							preamble += tempString + lineEnding;
						}
						mp = headerStack.pop();
						mp.setPreamble(preamble);
						headerStack.push(mp);
						break;

					// If there's an epilogue, get the multipartheader off
					// the top of the stack, set it, and push back on the stack
					case T_EPILOGUE:
						BufferedReader epilogueReader = new BufferedReader(
								stream.getReader());

						String tempEpilogueString;
						String epilogue = "";
						while ((tempEpilogueString = epilogueReader.readLine()) != null) {
							epilogue += tempEpilogueString + lineEnding;
						}
						mp = headerStack.pop();
						mp.setEpilogue(epilogue);
						headerStack.push(mp);
						break;

					// Create a new singlepartheader, set the headers,
					// set the content_data
					case T_BODY:
						EnkiveMessagePartSingle single = new EnkiveMessagePartSingle();
						single.setLineEnding(lineEnding);
						final String transferEncoding = stream.getBodyDescriptor()
								.getTransferEncoding();
						single.setContent(IOUtils.toString(stream.getInputStream()));

						single.setContentTransferEncoding(transferEncoding);

						single.setOriginalHeaders(headers.toString());
						// If we're working with a multipart message,
						// pop, add the singlepartheader, and push.
						// Otherwise just set the singlepartheader
						if (isMultiPart) {
							mp = headerStack.pop();
							mp.addMessagePart(single);
							headerStack.push(mp);
						} else
							enkiveMessage.setMessageBody(single);
						break;

					// If we've reached the end of a multipart, it could
					// be a nested multipart. In that case we'll need to
					// Add the nested multipart to the multipart a level above.
					// If not nested, we've reached the end of the content headers
					// so set it.
					case T_END_MULTIPART:
						mp = headerStack.pop();
						if (headerStack.isEmpty()) {
							enkiveMessage.setMessageBody(mp);
						} else {
							EnkiveMessagePartMulti mp2 = headerStack.pop();
							mp2.addMessagePart(mp);
							headerStack.push(mp2);
						}
						break;
					default:
						// ignore other tags
						break;
				} // switch
			} // for

			return enkiveMessage;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
