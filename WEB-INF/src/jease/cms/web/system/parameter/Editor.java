/*
    Copyright (C) 2013 maik.jablonski@jease.org

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package jease.cms.web.system.parameter;

import jease.Names;
import jease.cmf.web.JeaseSession;
import jease.cms.domain.Parameter;
import jfix.db4o.Database;
import jfix.functor.Predicate;
import jfix.util.I18N;
import jfix.zk.Codearea;
import jfix.zk.Div;
import jfix.zk.ObjectEditor;
import jfix.zk.Textfield;

import org.apache.commons.lang3.StringUtils;

public class Editor extends ObjectEditor<Parameter> {

	Textfield key = new Textfield();
	Textfield singleValue = new Textfield();
	Codearea multiValue = new Codearea();

	public Editor() {
		multiValue.setHeight((100 + (Integer) JeaseSession
				.get(Names.JEASE_CMS_HEIGHT) / 3) + "px");
	}

	public void init() {
		add(I18N.get("Key"), key);
		add(I18N.get("Value"), new Div(singleValue, multiValue));
	}

	public void load() {
		boolean multiline = StringUtils.isBlank(getObject().getValue())
				|| getObject().getValue().contains("\n");
		key.setText(getObject().getKey());
		singleValue.setText(getObject().getValue());
		singleValue.setVisible(!multiline);
		multiValue.setText(getObject().getValue());
		multiValue.setVisible(multiline);
		adjustSyntax();
	}

	private void adjustSyntax() {
		if (multiValue.getText().contains("public class")) {
			multiValue.setSyntax("java");
			return;
		}
		if (multiValue.getText().startsWith("<")) {
			multiValue.setSyntax("html");
			return;
		}
		multiValue.setSyntax("txt");
	}

	public void save() {
		getObject().setKey(key.getText());
		if (singleValue.isVisible()) {
			getObject().setValue(singleValue.getText());
		} else {
			getObject().setValue(multiValue.getText());
		}
		Database.save(getObject());
	}

	public void delete() {
		Database.delete(getObject());
	}

	public void validate() {
		validate(key.isEmpty(), I18N.get("Key_is_required"));
		validate(!Database.isUnique(getObject(), new Predicate<Parameter>() {
			public boolean test(Parameter parameter) {
				return parameter.getKey().equals(key.getText());
			}
		}), I18N.get("Key_must_be_unique"));
	}
}
