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

package org.uma.jmetal.util.pseudorandom;

import java.io.Serializable;

/**
 * @author Antonio J. Nebro
 * @version 0.1
 */
public interface PseudoRandomGenerator extends Serializable {
  public int nextInt(int lowerBound, int upperBound) ;
  public double nextDouble(double lowerBound, double upperBound) ;
  public double nextDouble() ;
  public void setSeed(long seed) ;
  public long getSeed() ;
  public String getName() ;
}
