/**
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 */
package com.thizzer.spkt.extensions

fun String?.quoted(): String {
    return '"' + (this ?: "") + '"'
}