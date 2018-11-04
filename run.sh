#!/bin/bash
ant 
java -cp ABAGAIL.jar opt.test.OnlineNewsPopularityRHC | tee -a rhc.log
