import org.jggug.kobo.UseBinaryLiteral

@UseBinaryLiteral
class Test {
  def test() {
    println $b000000001
    println $b01_001_0001
  }
}

test = new Test()
test.test()
