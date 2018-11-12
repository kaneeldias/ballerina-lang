const boolean someBoolean = 10;
const int someInt = "ABC";
const byte someByte = 500;
const float someFloat = true;
const decimal someDeciaml = true;
const string someString = 120;

// Assigning var ref.
string s = "Ballerina";
const string name = s;
public const string name2 = s;

// Assigning var ref.
int a = 10;
const age = a;
public const age2 = a;

// Updating const.
const x = 10;
const int y = 20;

function testAssignment() {
    x = 1;
    y = 2;
}

// Updating const in worker.
function testWorkerInteractions() {
    worker w1 {
        x <- w2;
    }
    worker w2 {
        30 -> w1;
    }
}

const string sVar = 10;

const string m = { name: "Ballerina" };

// Redeclared constant.
const abc = "abc";

const abc = "Ballerina";

// Redeclared variable.
const def = "def";

function test() {
    string def = "def";
}

// Incompatible types.
type ACTION "GET";

type XYZ "XYZ";

const xyz = "XYZ";

function testInvalidTypes() returns ACTION {
    ACTION action = xyz;
    return action;
}

// Built-in function invocation.
function testInvalidInvocation() {
    string lowercase = xyz.toLower();
}

// -----------------------------------------------------------

const byteWithoutType = 120;

function testByteWithoutType() returns byte {
    return byteWithoutType;
}

// -----------------------------------------------------------

const D = "D";

const E = "E";

const F = "F";

type G E|F;

type H D|E;

const h = "D";

function testImproperSubset() returns G {
    G g = h;
    return g;
}

// -----------------------------------------------------------

// Cyclic dependency.
type UVW UVW;

// -----------------------------------------------------------

// Cyclic dependency and unknown type.
type IJK IJK|"R"|SSS|"S";

// -----------------------------------------------------------

// Cyclic dependency and multiple unknown types.
type LMN OPQ|"QRS"|RST|STU;

type OPQ LMN|"STU"|RST;

// -----------------------------------------------------------

// Complex cyclic dependency.
type ACE BDF;

type BDF "AAA"|CEG|"TTT"|DFH;

type CEG ACE|"UUU"|EGI;

type DFH "DFH";

type EGI BDF|ACE;

// -----------------------------------------------------------

// Type node's type undefined.
type MNO PQ;

// -----------------------------------------------------------

// Type node's one of member's type not available.
type JKL J|STU;

const J = "J";

type STU S|T|U;

const S = "S";

// -----------------------------------------------------------

function testInvalidConstUsage() {
    string wxy = name2;
}

// -----------------------------------------------------------

const boolean booleanWithType = false;

type BooleanTypeWithType booleanWithType;

function testBooleanTypeWithType() returns BooleanTypeWithType {
    BooleanTypeWithType t = true;
    return t;
}

const booleanWithoutType = true;

type BooleanTypeWithoutType booleanWithoutType;

function testBooleanTypeWithoutType() returns BooleanTypeWithoutType {
    BooleanTypeWithoutType t = false;
    return t;
}

// -----------------------------------------------------------

const int intWithType = 40;

type IntTypeWithType intWithType;

function testIntTypeWithType() returns IntTypeWithType {
    IntTypeWithType t = 100;
    return t;
}

const intWithoutType = 20;

type IntTypeWithoutType intWithoutType;

function testIntTypeWithoutType() returns IntTypeWithoutType {
    IntTypeWithoutType t = 100;
    return t;
}

// -----------------------------------------------------------

const byte byteWithType = 240;

type ByteTypeWithType byteWithType;

function testByteTypeWithType() returns ByteTypeWithType {
    ByteTypeWithType t = 120;
    return t;
}

// -----------------------------------------------------------

const float floatWithType = 4.0;

type FloatTypeWithType floatWithType;

function testFloatTypeWithType() returns FloatTypeWithType {
    FloatTypeWithType t = 10.0;
    return t;
}

const floatWithoutType = 2.0;

type FloatTypeWithoutType floatWithoutType;

function testFloatTypeWithoutType() returns FloatTypeWithoutType {
    FloatTypeWithoutType t = 10.0;
    return t;
}

// -----------------------------------------------------------

const decimal decimalWithType = 4.0;

type DecimalTypeWithType decimalWithType;

function testDecimalTypeWithType() returns DecimalTypeWithType {
    DecimalTypeWithType t = 10.0;
    return t;
}

// -----------------------------------------------------------

const string stringWithType = "Ballerina is awesome";

type StringTypeWithType stringWithType;

function testStringTypeWithType() returns StringTypeWithType {
    StringTypeWithType t = "random text";
    return t;
}

const stringWithoutType = "Ballerina rocks";

type StringTypeWithoutType stringWithoutType;

function testStringTypeWithoutType() returns StringTypeWithoutType {
    StringTypeWithoutType t = "random text";
    return t;
}
