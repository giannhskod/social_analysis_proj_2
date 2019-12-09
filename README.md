# # Social Network Analysis Project 2

## Full-Name: Giannis Kontogeorgos
## Student-Id: P3352807


## *Description*
This project implements the Boggle Board game using Apache Giraph which implements a Pregel like Graph computation programming model.
For the purpose of the exercise the project is splitted into 2 parts

1. **part1**: Contains the output logfile of the default MapReduce Giraph's example.It has the job's task_id as filenme.
2. **part2**: Contains the implementation of the Boggle Board game.

The project is written in Java and it has the below structure.
### .java files
* **/src/gr/aueb/sna/GiraphAppRunner.java** is the runtime application class. This is the Java application that should be run.
* **/src/org/apache/giraph/examples/SimpleBoggleComputation.java** contains the main logic of the project implementation. 
It extends the *BasicComputation* Giraph's graph and overides the **compute** method that will be run for each vertex per every superstep. 
* **/src/org/apache/giraph/io/formats/BoggleInputFormat.java** constructs the input format of each vertex.
* **/src/org/apache/giraph/io/formats/BoggleVertex.java** implements the default Giraph's *Vertex* class and overrides all the required
methods that customizes the Vertex based on the requirements of the problem.

### Input & Output folders/files
* **boggle.txt** contains the simple Boggle board example (3x3) matrix format.
* **boggle_4x4.txt** contains an extended Boggle board with more letters and a 4x4 formation.
* **boggleOutput/** is the output folder that will contains the results of a run when *boggle.txt* is used.
* **boggleOutput_4x4/** is the output folder that will contains the results of a run when *boggle_4x4.txt* is used.
* **dictionaries/simple_dict.txt** is a simple dictionary with 5 words.
* **dictionaries/full_dict.txt** is an extended dictionary with > 200000 words.

> The values of the ** boggleOutput*/ ** files contain the results of the below configured runs
> 1. *boggleOutput/* -> boogle.txt + simple_dict.txt
> 2. *boggleOutput_4x4/* -> boogle_4x4.txt + full_dict.txt

## *Instructions*

If you run the project throw an Eclipse workspace then the configured output folder has to be deleted. The programm will 
auto-generate it at the end of the computations.
