/*
 * Copyright (C) 2008  Distributed Computing System (DCS) Group, Computer
 * Science Department - University of Piemonte Orientale, Alessandria (Italy).
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package it.unipmn.di.dcs.sharegrid.web.faces.el;

import javax.el.ELContext;
import javax.el.ValueExpression;

/**
 * EL used for indexing collection values.
 *
 * @author <a href="mailto:marco.guazzone@gmail.com">Marco Guazzone</a>
 */
public final class IndexedValueExpression extends ValueExpression
{
    private static final long serialVersionUID = 1L;
    protected final Integer i;
    protected final ValueExpression orig;

    public IndexedValueExpression(ValueExpression orig, int i) {
        this.i = new Integer(i);
        this.orig = orig;
    }

    public Object getValue(ELContext context) {
        Object base = this.orig.getValue(context);
        if (base != null) {
            context.setPropertyResolved(false);
            return context.getELResolver().getValue(context, base, i);
        }
        return null;
    }

    public void setValue(ELContext context, Object value) {
        Object base = this.orig.getValue(context);
        if (base != null) {
            context.setPropertyResolved(false);
            context.getELResolver().setValue(context, base, i, value);
        }
    }

    public boolean isReadOnly(ELContext context) {
        Object base = this.orig.getValue(context);
        if (base != null) {
            context.setPropertyResolved(false);
            return context.getELResolver().isReadOnly(context, base, i);
        }
        return true;
    }

    public Class getType(ELContext context) {
        Object base = this.orig.getValue(context);
        if (base != null) {
            context.setPropertyResolved(false);
            return context.getELResolver().getType(context, base, i);
        }
        return null;
    }

    public Class getExpectedType() {
        return Object.class;
    }

    public String getExpressionString() {
        return this.orig.getExpressionString();
    }

    public boolean equals(Object obj) {
        return this.orig.equals(obj);
    }

    public int hashCode() {
        return this.orig.hashCode();
    }

    public boolean isLiteralText() {
        return false;
    }
}

