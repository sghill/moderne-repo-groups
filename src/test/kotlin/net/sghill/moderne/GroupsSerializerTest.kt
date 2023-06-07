package net.sghill.moderne

import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito.given
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import kotlin.test.Test

@ExtendWith(MockitoExtension::class)
class GroupsSerializerTest {
    @Mock
    lateinit var clock: Clock<Long>
    
//    @Test
//    fun shouldSerialize() {
//        given(clock.now()).willReturn(1686000716042)
//        val serializer = GroupsSerializer(clock)
//        val actual = serializer.serialize(PluginGroupSpec(
//            "plugins-group-test",
//            setOf("naginator")
//        ))
//        val expected = """
//            
//        """.trimIndent()
//    }
}
