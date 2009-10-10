import org.jggug.kobo.Define

@Define(symbol="それ", value="it")
@Define(symbol="これ", value="this")
@Define(symbol="表示", value="println")
class Test {
  def a;
  def f(hoge, fuga) {
    int fefe
    (1..10).each {
      表示 それ
    }
  }
}


test = new Test()
test.f(1,2)
