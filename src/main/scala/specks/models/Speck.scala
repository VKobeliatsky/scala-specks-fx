package specks.models

class Speck
case class Value(var value: Int) extends Speck
case class Empty() extends Speck
