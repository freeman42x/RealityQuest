import akka.actor.ActorSystem
import akka.actor.Props
import akka.testkit.TestKit
import com.github.razvanpanda.realityquest.sensors.LockStatus
import com.github.razvanpanda.realityquest.sensors.LockStatus.GetLockStatus
import com.sun.jna.{Native, Library}
import org.scalatest.{WordSpecLike, Matchers, BeforeAndAfterAll}
import akka.testkit.ImplicitSender

class LockStatusSpec(_system: ActorSystem) extends TestKit(_system) with ImplicitSender with WordSpecLike with Matchers with BeforeAndAfterAll
{
    def this() = this(ActorSystem())
    val lockStatusRef = system.actorOf(Props[LockStatus])

    override def afterAll()
    {
        TestKit.shutdownActorSystem(system)
    }

    "A LockStatus actor" must
    {
        "reply with false when the workstation is unlocked" in
        {
            lockStatusRef ! GetLockStatus
            expectMsg(false)
        }
    }

    trait User32 extends Library
    {
        def LockWorkStation: Boolean
    }

    "A LockStatus actor" must
    {
        "reply with true when the workstation is locked" ignore
        {
            Thread.sleep(1000)
            Native.loadLibrary("user32", classOf[User32]).asInstanceOf[User32].LockWorkStation
            Thread.sleep(1000)
            lockStatusRef ! GetLockStatus
            expectMsg(true)
        }
    }
}