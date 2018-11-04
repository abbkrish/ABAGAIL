#!/bin/bash
ant 
rm *.log
java -cp ABAGAIL.jar opt.test.OnlineNewsPopularityRHC 10 | tee -a rhc.log
java -cp ABAGAIL.jar opt.test.OnlineNewsPopularitySA 1E5 0.99 | tee -a sa1.log
java -cp ABAGAIL.jar opt.test.OnlineNewsPopularitySA 1E8 0.99 | tee -a sa2.log
java -cp ABAGAIL.jar opt.test.OnlineNewsPopularitySA 1E10 0.99 | tee -a sa3.log
java -cp ABAGAIL.jar opt.test.OnlineNewsPopularitySA 1E12 0.99 | tee -a sa4.log
java -cp ABAGAIL.jar opt.test.OnlineNewsPopularitySA 1E15 0.99 | tee -a sa5.log
java -cp ABAGAIL.jar opt.test.OnlineNewsPopularitySA 1E10 0.20 | tee -a sa6.log
java -cp ABAGAIL.jar opt.test.OnlineNewsPopularitySA 1E10 0.30 | tee -a sa7.log
java -cp ABAGAIL.jar opt.test.OnlineNewsPopularitySA 1E10 0.50 | tee -a sa8.log
java -cp ABAGAIL.jar opt.test.OnlineNewsPopularitySA 1E10 0.60 | tee -a sa9.log
java -cp ABAGAIL.jar opt.test.OnlineNewsPopularitySA 1E10 0.80 | tee -a sa10.log
java -cp ABAGAIL.jar opt.test.OnlineNewsPopularitySA 1E10 0.95 | tee -a sa11.log
java -cp ABAGAIL.jar opt.test.OnlineNewsPopularityGA 25 10 10 | tee -a ga1.log
java -cp ABAGAIL.jar opt.test.OnlineNewsPopularityGA 50 10 10 | tee -a ga2.log
java -cp ABAGAIL.jar opt.test.OnlineNewsPopularityGA 75 10 10 | tee -a ga3.log
java -cp ABAGAIL.jar opt.test.OnlineNewsPopularityGA 100 10 10 | tee -a ga4.log
java -cp ABAGAIL.jar opt.test.OnlineNewsPopularityGA 125 10 10 | tee -a ga5.log
java -cp ABAGAIL.jar opt.test.OnlineNewsPopularityGA 125 20 50 | tee -a ga6.log
java -cp ABAGAIL.jar opt.test.OnlineNewsPopularityGA 125 30 50 | tee -a ga7.log
java -cp ABAGAIL.jar opt.test.OnlineNewsPopularityGA 125 50 50 | tee -a ga8.log
java -cp ABAGAIL.jar opt.test.OnlineNewsPopularityGA 125 75 50 | tee -a ga9.log
java -cp ABAGAIL.jar opt.test.OnlineNewsPopularityGA 125 100 50 | tee -a ga10.log
java -cp ABAGAIL.jar opt.test.OnlineNewsPopularityGA 125 50 25 | tee -a ga11.log
java -cp ABAGAIL.jar opt.test.OnlineNewsPopularityGA 125 50 35 | tee -a ga12.log
java -cp ABAGAIL.jar opt.test.OnlineNewsPopularityGA 125 50 50 | tee -a ga13.log
java -cp ABAGAIL.jar opt.test.OnlineNewsPopularityGA 125 50 75 | tee -a ga14.log
java -cp ABAGAIL.jar opt.test.OnlineNewsPopularityGA 125 50 100 | tee -a ga15.log






