/**
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 */
import com.thizzer.spkt.dsl.swiftPackage
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class SwiftPackageTest {

    @Test
    fun emptyPackageTest() {
        val manifest = swiftPackage("EmptyPackage") { }
        Assertions.assertEquals("Package(\n\tname: \"EmptyPackage\"\n)", manifest.toString())
    }
}