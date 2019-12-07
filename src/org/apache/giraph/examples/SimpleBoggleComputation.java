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
import org.apache.giraph.edge.Edge;
import org.apache.giraph.graph.Vertex;
import org.apache.giraph.examples.SimpleBoggleComputation.TextArrayListWritable;
import org.apache.giraph.utils.ArrayListWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

/**
 * Demonstrates the basic Pregel dispersion implementation.
 */
@Algorithm(
    name = "Boggle",
    description = "Sets the vertex value of each vertex to a list of boggle words ending in that vertex"
)
public class SimpleBoggleComputation extends BasicComputation<
    Text, TextArrayListWritable, NullWritable, TextArrayListWritable> {

	
	private static final TreeSet<String> dictionary = new TreeSet<String>(Arrays.asList("GEEKS", "SOCIAL", "NETWORK", "ANALYSIS", "QUIZ"));
	
	/** Class logger */
	  private static final Logger LOG =
	      Logger.getLogger(SimpleBoggleComputation.class);
	  
  @Override
  public void compute(
      Vertex<Text, TextArrayListWritable, NullWritable> vertex,
      Iterable<TextArrayListWritable> messages) throws IOException {
	  if(getSuperstep() == 0){
		  TextArrayListWritable aw = new TextArrayListWritable();
		  aw.add(new Text(vertex.getId().toString().substring(0,1)));
		  aw.add(vertex.getId());
		  sendMessageToAllEdges(vertex, aw);
	  }
	  vertex.voteToHalt();
  }
  
  
  /** Utility class for delivering the array of vertices THIS vertex
    * should connect with to close triangles with neighbors */
  public static class TextArrayListWritable
    extends ArrayListWritable<Text> {
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
