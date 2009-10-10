import org.jggug.kobo.UseSourceInfoSymbol

@UseSourceInfoSymbol
class Test {
  def foo(hoge, fuga) {
    println __FILE__+':'+__LINE__
    println __FILE__+':'+__LINE__
    println __FILE__+':'+__LINE__
    println __FILE__+':'+__LINE__
    println __FILE__+':'+__LINE__
    println __FILE__+':'+__LINE__
    println __FILE__+':'+__LINE__

    println __FILE__+':'+__LINE__
    println __FILE__+':'+__LINE__
    println __FILE__+':'+__LINE__
    println __FILE__+':'+__LINE__
    println __FILE__+':'+__LINE__
    println __CLASS__+':'+__METHOD__
  }
}

test = new Test()
test.foo(1,2)
