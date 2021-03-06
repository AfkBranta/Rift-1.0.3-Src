package javassist.expr;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtBehavior;
import javassist.CtClass;
import javassist.NotFoundException;
import javassist.bytecode.BadBytecode;
import javassist.bytecode.Bytecode;
import javassist.bytecode.CodeAttribute;
import javassist.bytecode.CodeIterator;
import javassist.bytecode.ConstPool;
import javassist.bytecode.MethodInfo;
import javassist.compiler.CompileError;
import javassist.compiler.Javac;
import javassist.compiler.JvstCodeGen;
import javassist.compiler.JvstTypeChecker;
import javassist.compiler.ProceedHandler;
import javassist.compiler.ast.ASTList;

public class Cast extends Expr {

    protected Cast(int pos, CodeIterator i, CtClass declaring, MethodInfo m) {
        super(pos, i, declaring, m);
    }

    public CtBehavior where() {
        return super.where();
    }

    public int getLineNumber() {
        return super.getLineNumber();
    }

    public String getFileName() {
        return super.getFileName();
    }

    public CtClass getType() throws NotFoundException {
        ConstPool cp = this.getConstPool();
        int pos = this.currentPos;
        int index = this.iterator.u16bitAt(pos + 1);
        String name = cp.getClassInfo(index);

        return this.thisClass.getClassPool().getCtClass(name);
    }

    public CtClass[] mayThrow() {
        return super.mayThrow();
    }

    public void replace(String statement) throws CannotCompileException {
        this.thisClass.getClassFile();
        ConstPool constPool = this.getConstPool();
        int pos = this.currentPos;
        int index = this.iterator.u16bitAt(pos + 1);
        Javac jc = new Javac(this.thisClass);
        ClassPool cp = this.thisClass.getClassPool();
        CodeAttribute ca = this.iterator.get();

        try {
            CtClass[] e = new CtClass[] { cp.get("java.lang.Object")};
            CtClass retType = this.getType();
            int nameVar = ca.getMaxLocals();

            jc.recordParams("java.lang.Object", e, true, nameVar, this.withinStatic());
            int retVar = jc.recordReturnType(retType, true);

            jc.recordProceed(new Cast.ProceedForCast(index, retType));
            checkResultValue(retType, statement);
            Bytecode bytecode = jc.getBytecode();

            storeStack(e, true, nameVar, bytecode);
            jc.recordLocalVariables(ca, pos);
            bytecode.addConstZero(retType);
            bytecode.addStore(retVar, retType);
            jc.compileStmnt(statement);
            bytecode.addLoad(retVar, retType);
            this.replace0(pos, bytecode, 3);
        } catch (CompileError compileerror) {
            throw new CannotCompileException(compileerror);
        } catch (NotFoundException notfoundexception) {
            throw new CannotCompileException(notfoundexception);
        } catch (BadBytecode badbytecode) {
            throw new CannotCompileException("broken method");
        }
    }

    static class ProceedForCast implements ProceedHandler {

        int index;
        CtClass retType;

        ProceedForCast(int i, CtClass t) {
            this.index = i;
            this.retType = t;
        }

        public void doit(JvstCodeGen gen, Bytecode bytecode, ASTList args) throws CompileError {
            if (gen.getMethodArgsLength(args) != 1) {
                throw new CompileError("$proceed() cannot take more than one nameeter for cast");
            } else {
                gen.atMethodArgs(args, new int[1], new int[1], new String[1]);
                bytecode.addOpcode(192);
                bytecode.addIndex(this.index);
                gen.setType(this.retType);
            }
        }

        public void setReturnType(JvstTypeChecker c, ASTList args) throws CompileError {
            c.atMethodArgs(args, new int[1], new int[1], new String[1]);
            c.setType(this.retType);
        }
    }
}
