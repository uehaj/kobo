import org.jggug.kobo.WithTimeout

class Test {

  int x = 5

  @WithTimeout(3)
  def test1(String s, int i) {
    Thread.sleep(2000)
    println "test1 done"
  }

  @WithTimeout(1)
  def test2() {
    Thread.sleep(2000)
    println "test2 done"
  }

}

test = new Test()
test.test1("a", 3)
test.test2()
