/*
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301  USA
 */

/**
 * Deep object comparison
 */
Object.deepEquals = function (x, y) {
  if (x === y) return true;
  // if both x and y are null or undefined and exactly the same

  if (!(x instanceof Object) || !(y instanceof Object)) return false;
  // if they are not strictly equal, they both need to be Objects

  if (x.constructor !== y.constructor) return false;
  // they must have the exact same prototype chain, the closest we can do is
  // test there constructor.

  for ( var p in x) {
    if (!x.hasOwnProperty (p)) continue;
    // other properties were tested using x.constructor === y.constructor

    if (!y.hasOwnProperty (p)) return false;
    // allows to compare x[ p ] and y[ p ] when set to undefined

    if (x[p] === y[p]) continue;
    // if they have the same strict value or identity then they are equal

    if (typeof (x[p]) !== "object") return false;
    // Numbers, Strings, Functions, Booleans must be strictly equal

    if (!Object.deepEquals (x[p], y[p])) return false;
    // Objects and Arrays must be tested recursively
  }

  for ( var p in y)
    if (y.hasOwnProperty (p) && !x.hasOwnProperty (p)) return false;
  // allows x[ p ] to be set to undefined
  return true;
};