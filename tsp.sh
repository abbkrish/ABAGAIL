#!/bin/bash
ant 
java -cp ABAGAIL.jar opt.test.TravelingSalesmanTest 75  1000
java -cp ABAGAIL.jar opt.test.TravelingSalesmanTest 100 1000
java -cp ABAGAIL.jar opt.test.TravelingSalesmanTest 125 1000
java -cp ABAGAIL.jar opt.test.TravelingSalesmanTest 150 1000
java -cp ABAGAIL.jar opt.test.TravelingSalesmanTest 200 1000