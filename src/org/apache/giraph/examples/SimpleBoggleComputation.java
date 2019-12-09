/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.giraph.examples;

import org.apache.giraph.graph.BasicComputation;
import org.apache.giraph.edge.MutableEdge;
import org.apache.giraph.graph.Vertex;
import org.apache.giraph.examples.SimpleBoggleComputation.TextArrayListWritable;
import org.apache.giraph.utils.ArrayListWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.TreeSet;

/**
 * Demonstrates the basic Pregel dispersion implementation.
 */
@Algorithm(name = "Boggle", description = "Sets the vertex value of each vertex to a list of boggle words ending in that vertex")
public class SimpleBoggleComputation
		extends BasicComputation<Text, TextArrayListWritable, NullWritable, TextArrayListWritable> {

	/*
	 * The <readSortedTreeSet> class method reads from the given file path and
	 * generates a TreeSet with the dictionary that while be used at runtime.
	 * The <simple_dict.txt> contains the example dictionary that is given from
	 * the exercise. The <full_dict.txt> contains a complete dictionary that is
	 * found from the https://raw.githubusercontent.com/hillmanov/
	 * go-boggle/master/dictionary.txt
	 */
	// private static final TreeSet<String> dictionary = SimpleBoggleComputation
	// .readTreeSet("dictionaries/simple_dict.txt", " ");
	private static final TreeSet<String> dictionary = SimpleBoggleComputation.readTreeSet("dictionaries/full_dict.txt",
			" ");

	/** Class logger */
	private static final Logger LOG = Logger.getLogger(SimpleBoggleComputation.class);

	public static final TreeSet<String> readTreeSet(String filePath, String delimiter) {
		BufferedReader br = null;
		TreeSet<String> sortedSurfaceFormSet = new TreeSet<String>();
		try {
			String sCurrentLine;
			FileInputStream fr = new FileInputStream(filePath);
			InputStreamReader isr = new InputStreamReader(fr, Charset.forName("UTF-8"));
			br = new BufferedReader(isr);
			while ((sCurrentLine = br.readLine()) != null) {
				String[] nextLine = sCurrentLine.split(delimiter);
				sortedSurfaceFormSet.add(new String(nextLine[0]).toLowerCase());
			}
			br.close();
		} catch (IOException e) {
			LOG.info(" Class : " + SimpleBoggleComputation.class.getSimpleName()
					+ " and method : readSortedTreeSet, Trace :" + e);
		}
		return sortedSurfaceFormSet;
	}

	@Override
	public void compute(Vertex<Text, TextArrayListWritable, NullWritable> vertex,
			Iterable<TextArrayListWritable> messages) throws IOException {
		// Get the vertex letter as lowerCase.
		Text vertexTextValue = new Text(vertex.getId().toString().substring(0, 1).toLowerCase());

		// Case of first superstep. Propagate to all connected edges of the
		// vertex.
		if (getSuperstep() == 0) {
			TextArrayListWritable newMessage = this.createNewMessage(vertexTextValue, vertex.getId());
			sendMessageToAllEdges(vertex, newMessage);
		} else {
			/*
			 * If true at the end of the calculations of the passes <messages>
			 * then the vertex should halt it's work.
			 */
			Boolean halt = true;

			for (TextArrayListWritable message : messages) {
				Text messageWord = message.get(0);
				Text verticesIds = message.get(1);
				Boolean shouldHalt = false;

				/* Check if the VertexId is already contained in the message. */
				for (String vertexId : verticesIds.toString().split(", ")) {
					if (vertexId.contentEquals(vertex.getId().toString())) {
						shouldHalt = true;
						break;
					}
				}

				if (!shouldHalt) {
					String newWordMessage = messageWord.toString() + vertexTextValue.toString();
					String newVertexIds = verticesIds.toString() + ", " + vertex.getId().toString();

					/*
					 * Check if <newWordMessage>(incoming message + vertex
					 * letter) is one of the dictionary words
					 */
					if (SimpleBoggleComputation.dictionary.contains(newWordMessage)) {
						TextArrayListWritable vertexValue = vertex.getValue();
						vertexValue.add(new Text(newWordMessage));
						vertex.setValue(vertexValue);
						shouldHalt = true;

						/*
						 * Check if <newWordMessage>(incoming message + vertex
						 * letter) is a prefix of a dictionary word
						 */
					} else if (this.isTreeSetPrefix(SimpleBoggleComputation.dictionary, newWordMessage) != null) {
						TextArrayListWritable newMessage = this.createNewMessage(new Text(newWordMessage),
								new Text(newVertexIds));

						/*
						 * Find the vertex edges that are not already contained
						 * in the message's history and propagate the
						 * <newWordMessage> to them.
						 */
						for (MutableEdge<Text, NullWritable> edge : vertex.getMutableEdges()) {
							if (!verticesIds.toString().contains(edge.toString())) {
								sendMessage(edge.getTargetVertexId(), newMessage);
							}
						}
					} else {
						shouldHalt = true;
					}
				}
				halt &= shouldHalt;
			}
			if (halt) {
				vertex.voteToHalt();
			}
		}
	}

	/*
	 * Utility method that adds to a new TextArrayListWritable instance the
	 * MessageValue and the concatenated vertices ids.
	 */
	public TextArrayListWritable createNewMessage(Text wordMessage, Text VertexId) {
		TextArrayListWritable aw = new TextArrayListWritable();
		aw.add(wordMessage);
		aw.add(VertexId);
		return aw;
	}

	/*
	 * Utility method that checks if a given <word> is a prefix for a given
	 * <dict>(dictionary) word.
	 */
	public String isTreeSetPrefix(TreeSet<String> dict, String word) {
		for (String entry : dict) {
			if (entry.startsWith(word)) {
				return entry;
			}
		}
		return null;
	}

	/**
	 * Utility class for delivering the array of vertices THIS vertex should
	 * connect with to close triangles with neighbors
	 */
	public static class TextArrayListWritable extends ArrayListWritable<Text> {
		private static final long serialVersionUID = -7220517688447798587L;

		/** Default constructor for reflection */
		public TextArrayListWritable() {
			super();
		}

		/** Set storage type for this ArrayListWritable */
		@Override
		@SuppressWarnings("unchecked")
		public void setClass() {
			setClass(Text.class);
		}
	}
}
