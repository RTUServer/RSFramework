package kr.rtuserver.lib.common.api.cdi.beans.classholders;

import kr.rtuserver.lib.common.api.cdi.beans.visitors.ClassHolderVisitor;

/**
 * @author Mihai Alexandru
 * @date 01.09.2018
 */
public interface ClassHolder {

    void accept(ClassHolderVisitor classHolderVisitor);

    Class<?> getBeanClass();
}