//  GenerationalDistance.java
//
//  Author:
//       Antonio J. Nebro <antonio@lcc.uma.es>
//       Juan J. Durillo <durillo@lcc.uma.es>
//
//  Copyright (c) 2011 Antonio J. Nebro, Juan J. Durillo
//
//  This program is free software: you can redistribute it and/or modify
//  it under the terms of the GNU Lesser General Public License as published by
//  the Free Software Foundation, either version 3 of the License, or
//  (at your option) any later version.
//
//  This program is distributed in the hope that it will be useful,
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//  GNU Lesser General Public License for more details.
// 
//  You should have received a copy of the GNU Lesser General Public License
//  along with this program.  If not, see <http://www.gnu.org/licenses/>.

package org.uma.jmetal.qualityindicator.impl;

import org.uma.jmetal.qualityindicator.QualityIndicator;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.front.Front;
import org.uma.jmetal.util.front.imp.ArrayFront;
import org.uma.jmetal.util.front.imp.FrontUtils;

import java.util.List;

/**
 * This class implements the generational distance indicator. It can be used also
 * as a command line by typing:
 * "java org.uma.jmetal.qualityindicator.impl.GenerationalDistance <solutionFrontFile>
 * <trueFrontFile> <getNumberOfObjectives>"
 * Reference: Van Veldhuizen, D.A., Lamont, G.B.: Multiobjective Evolutionary
 * Algorithm Research: A History and Analysis.
 * Technical Report TR-98-03, Dept. Elec. Comput. Eng., Air Force
 * Inst. Technol. (1998)
 */
public class GenerationalDistance implements QualityIndicator {
  private static final String NAME = "GD" ;

  private static final double POW = 2.0;

  @Override
  public double execute(Front paretoFrontApproximation, Front trueParetoFront) {
    if (paretoFrontApproximation == null) {
      throw new JMetalException("The pareto front approximation object is null") ;
    } else if (trueParetoFront == null) {
      throw new JMetalException("The pareto front object is null");
    }

    return generationalDistance(paretoFrontApproximation, trueParetoFront) ;
  }

  @Override
  public double execute(List<? extends Solution> paretoFrontApproximation,
      List<? extends Solution> trueParetoFront) {

    if (paretoFrontApproximation == null) {
      throw new JMetalException("The pareto front approximation list is null") ;
    } else if (trueParetoFront == null) {
      throw new JMetalException("The pareto front list is null");
    }

    return this.generationalDistance(new ArrayFront(paretoFrontApproximation), new ArrayFront(trueParetoFront)) ;
  }

  /**
   * Returns the generational distance value for a given front
   *
   * @param front           The front
   * @param trueParetoFront The true pareto front
   */
  public double generationalDistance(Front front, Front trueParetoFront) {
    double[] maximumValue;
    double[] minimumValue;
    Front normalizedFront;
    Front normalizedParetoFront;

    // STEP 1. Obtain the maximum and minimum values of the Pareto front
    maximumValue = FrontUtils.getMaximumValues(trueParetoFront);
    minimumValue = FrontUtils.getMinimumValues(trueParetoFront);

    // STEP 2. Get the normalized front and true Pareto fronts
    /*normalizedFront = FrontUtils.getNormalizedFront(front,
        maximumValue,
        minimumValue);
    normalizedParetoFront = FrontUtils.getNormalizedFront(trueParetoFront,
        maximumValue,
        minimumValue);*/

    // STEP 3. Sum the distances between each point of the front and the
    // nearest point in the true Pareto front
    double sum = 0.0;
    for (int i = 0; i < front.getNumberOfPoints(); i++) {
      sum += Math.pow(FrontUtils.distanceToClosestPoint(front.getPoint(i),
    		  trueParetoFront), POW);
    }

    // STEP 4. Obtain the sqrt of the sum
    sum = Math.pow(sum, 1.0 / POW);

    // STEP 5. Divide the sum by the maximum number of points of the front
    double generationalDistance = sum / front.getNumberOfPoints();

    return generationalDistance;
  }

  @Override
  public String getName() {
    return NAME;
  }
}
