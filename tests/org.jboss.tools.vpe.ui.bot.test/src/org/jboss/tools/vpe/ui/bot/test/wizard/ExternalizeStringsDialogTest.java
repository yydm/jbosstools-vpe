/*******************************************************************************
 * Copyright (c) 2007-2010 Exadel, Inc. and Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Exadel, Inc. and Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.vpe.ui.bot.test.wizard;

import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotEditor;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotCheckBox;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotCombo;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTable;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotText;
import org.jboss.tools.vpe.messages.VpeUIMessages;
import org.jboss.tools.vpe.ui.bot.test.VPEAutoTestCase;
import org.jboss.tools.ui.bot.ext.SWTTestExt;
import org.jboss.tools.ui.bot.test.WidgetVariables;

public class ExternalizeStringsDialogTest extends VPEAutoTestCase {

	private final String FOLDER_TEXT_LABEL = "Enter or select the parent folder:"; //$NON-NLS-1$
	private final String INCORRECT_TABLE_VALUE = "Table value is incorrect"; //$NON-NLS-1$
	private final String CANNOT_FIND_PROPERTY_VALUE = "Cannot find 'Property Value' text field"; //$NON-NLS-1$
	private final String COMPLEX_TEXT = "!! HELLO ~ Input User, Name.Page ?" //$NON-NLS-1$
		+ " \r\n and some more text \r\n" //$NON-NLS-1$
		+ "@ \\# vc \\$ % yy^ &*(ghg ) _l-kk+mmm\\/fdg\\ " //$NON-NLS-1$
		+ "\t ;.df:,ee {df}df[ty]"; //$NON-NLS-1$
	private final String COMPLEX_KEY_RESULT = "HELLO_Input_User_Name_Page_and" + //$NON-NLS-1$
			"_some_more_text_vc_yy_ghg_l_kk_mmm_fdg_df_ee_df_df_ty"; //$NON-NLS-1$
	private final String COMPLEX_VALUE_RESULT = "!! HELLO ~ Input User, Name.Page ?" + //$NON-NLS-1$
	"      and some more text       @ \\# vc \\$ % yy^ &*(ghg ) _l-kk+mmm\\/fdg\\   ;.df:,ee {df}df[ty]"; //$NON-NLS-1$
	
	public ExternalizeStringsDialogTest() {
		super();
	}

	@Override
	protected void closeUnuseDialogs() {
		
	}

	@Override
	protected boolean isUnuseDialogOpened() {
		return false;
	}

	public void testExternalizeStringsDialog() throws Throwable {
		/*
		 * Open simple html file in order to get the VPE toolbar
		 */
		SWTBotEditor editor = SWTTestExt.packageExplorer.openFile(JBT_TEST_PROJECT_NAME,
				"WebContent", "pages", TEST_PAGE); //$NON-NLS-1$ //$NON-NLS-2$
		editor.setFocus();
		/*
		 * Select some text
		 */
		editor.toTextEditor().selectRange(7, 18, 4);
		/*
		 * Get toolbar button
		 */
		bot.toolbarButtonWithTooltip(VpeUIMessages.EXTERNALIZE_STRINGS).click();
		bot.shell(VpeUIMessages.EXTERNALIZE_STRINGS_DIALOG_TITLE).setFocus();
		bot.shell(VpeUIMessages.EXTERNALIZE_STRINGS_DIALOG_TITLE).activate();
		/*
		 * Check properties key and value fields
		 */
		SWTBotText defKeyText = bot.textWithLabelInGroup(
				VpeUIMessages.EXTERNALIZE_STRINGS_DIALOG_PROPERTIES_KEY, 
				VpeUIMessages.EXTERNALIZE_STRINGS_DIALOG_PROPS_STRINGS_GROUP);
		assertNotNull("Cannot find 'Property Key' text field", defKeyText); //$NON-NLS-1$
		assertText("User",defKeyText); //$NON-NLS-1$
		SWTBotText defValueText = bot.textWithLabelInGroup(
				VpeUIMessages.EXTERNALIZE_STRINGS_DIALOG_PROPERTIES_VALUE,
				VpeUIMessages.EXTERNALIZE_STRINGS_DIALOG_PROPS_STRINGS_GROUP);
		assertNotNull(CANNOT_FIND_PROPERTY_VALUE, defValueText);
		assertText("User", defValueText); //$NON-NLS-1$
		SWTBotCheckBox checkBox = bot.checkBox();
		assertNotNull("Cannot find checkbox '" //$NON-NLS-1$
				+ VpeUIMessages.EXTERNALIZE_STRINGS_DIALOG_NEW_FILE + "'", //$NON-NLS-1$
				checkBox);
		/*
		 * Check that "Next" button is disabled
		 */
		assertFalse("Checkbox should be unchecked.", //$NON-NLS-1$
				checkBox.isChecked());
		assertFalse("Next button should be disabled.", //$NON-NLS-1$
				bot.button(WidgetVariables.NEXT_BUTTON).isEnabled());
		/*
		 * Select existed resource bundle 
		 */
		SWTBotCombo combo = bot.comboBox();
		combo.setSelection(0);
		assertText("demo.Messages", combo); //$NON-NLS-1$
		/*
		 * Check table results
		 */
		SWTBotTable table = bot.table();
		assertNotNull("Table should exist", table); //$NON-NLS-1$
		assertEquals(INCORRECT_TABLE_VALUE, "header", table.cell(0, 0)); //$NON-NLS-1$
		assertEquals(INCORRECT_TABLE_VALUE, "Hello Demo Application", table.cell(0, 1)); //$NON-NLS-1$
		assertEquals(INCORRECT_TABLE_VALUE, "prompt_message", table.cell(1, 0)); //$NON-NLS-1$
		assertEquals(INCORRECT_TABLE_VALUE, "Name:", table.cell(1, 1)); //$NON-NLS-1$
		assertEquals(INCORRECT_TABLE_VALUE, "hello_message", table.cell(2, 0)); //$NON-NLS-1$
		assertEquals(INCORRECT_TABLE_VALUE, "Hello", table.cell(2, 1)); //$NON-NLS-1$
		/*
		 * Press OK and replace the text in the editor
		 */
		assertTrue("(OK) button should be enabled.", //$NON-NLS-1$
		bot.button(WidgetVariables.OK_BUTTON).isEnabled());
		bot.button(WidgetVariables.OK_BUTTON).click();
		/*
		 * Check replaced text
		 */
		editor.toTextEditor().selectRange(7, 18, 15);
		assertEquals("Replaced text is incorrect", "#{Message.User}", editor.toTextEditor().getSelection()); //$NON-NLS-1$ //$NON-NLS-2$
		/*
		 * Check that properties file has been updated
		 */
		SWTBotEditor editor2 = SWTTestExt.eclipse.openFile(
				JBT_TEST_PROJECT_NAME, "JavaSource", "demo", //$NON-NLS-1$ //$NON-NLS-2$
				"Messages.properties"); //$NON-NLS-1$
		editor2.toTextEditor().selectLine(3);
		String line = editor2.toTextEditor().getSelection();
		assertEquals("'Messages.properties' was updated incorrectly", "User=User", line); //$NON-NLS-1$ //$NON-NLS-2$
	}
	
	public void testNewFileInExternalizeStringsDialog() throws Throwable {
		/*
		 * Open simple html file in order to get the VPE toolbar
		 */
		SWTBotEditor editor = SWTTestExt.packageExplorer.openFile(JBT_TEST_PROJECT_NAME,
				"WebContent", "pages", TEST_PAGE); //$NON-NLS-1$ //$NON-NLS-2$
		editor.setFocus();
		/*
		 * Select some text
		 */
		editor.toTextEditor().selectRange(7, 12, 5);
		/*
		 * Get toolbar button
		 */
		bot.toolbarButtonWithTooltip(VpeUIMessages.EXTERNALIZE_STRINGS).click();
		bot.shell(VpeUIMessages.EXTERNALIZE_STRINGS_DIALOG_TITLE).setFocus();
		bot.shell(VpeUIMessages.EXTERNALIZE_STRINGS_DIALOG_TITLE).activate();
		
		/*
		 * Enable next page and check it 
		 */
		SWTBotCheckBox checkBox = bot.checkBox();
		assertNotNull("Cannot find checkbox '" //$NON-NLS-1$
				+ VpeUIMessages.EXTERNALIZE_STRINGS_DIALOG_NEW_FILE + "'", //$NON-NLS-1$
				checkBox);
		checkBox.select();
		assertTrue("Checkbox should be checked.", //$NON-NLS-1$
				checkBox.isChecked());
		assertTrue("Next button should be enabled.", //$NON-NLS-1$
				bot.button(WidgetVariables.NEXT_BUTTON).isEnabled());
		bot.button(WidgetVariables.NEXT_BUTTON).click();
		/*
		 * Create new file
		 */
		SWTBotText folderText = bot.textWithLabel(FOLDER_TEXT_LABEL);
		assertNotNull("'" + FOLDER_TEXT_LABEL + "' text field is not found", folderText); //$NON-NLS-1$ //$NON-NLS-2$
		folderText.setText(JBT_TEST_PROJECT_NAME+"/WebContent/pages"); //$NON-NLS-1$
		SWTBotText fileName = bot.textWithLabel("File name:"); //$NON-NLS-1$
		assertNotNull("'File Name:' text field is not found", fileName); //$NON-NLS-1$
		fileName.setText("externalize.properties"); //$NON-NLS-1$
		assertTrue("(OK) button should be enabled.", //$NON-NLS-1$
		bot.button(WidgetVariables.OK_BUTTON).isEnabled());
		bot.button(WidgetVariables.OK_BUTTON).click();
		/*
		 * Check that the text was replaced
		 */
		editor.toTextEditor().selectRange(7, 12, 9);
		assertEquals("Replaced text is incorrect", "#{.Input}", editor.toTextEditor().getSelection()); //$NON-NLS-1$ //$NON-NLS-2$
		/*
		 * Check that properties file has been created
		 */
		SWTBotEditor editor2 = SWTTestExt.eclipse.openFile(
				JBT_TEST_PROJECT_NAME, "WebContent", "pages", //$NON-NLS-1$ //$NON-NLS-2$
				"externalize.properties"); //$NON-NLS-1$
		editor2.toTextEditor().selectLine(1);
		String line = editor2.toTextEditor().getSelection();
		assertEquals("Created file is incorrect", "Input=Input", line); //$NON-NLS-1$ //$NON-NLS-2$
	}
	
	public void testEmptySelectionInExternalizeStringsDialog() throws Throwable {
		/*
		 * Open simple html file in order to get the VPE toolbar
		 */
		SWTBotEditor editor = SWTTestExt.packageExplorer.openFile(JBT_TEST_PROJECT_NAME,
				"WebContent", "pages", TEST_PAGE); //$NON-NLS-1$ //$NON-NLS-2$
		editor.setFocus();
		/*
		 * Select some text
		 */
		editor.toTextEditor().selectRange(13, 0, 1);
		/*
		 * There is an exception caused by the fact that
		 * line delimiter was selected.
		 * But for this test it's ok, so just ignore this exception.
		 */
		setException(null);
		/*
		 * Activate the dialog
		 */
		bot.toolbarButtonWithTooltip(VpeUIMessages.EXTERNALIZE_STRINGS).click();
		bot.shell(VpeUIMessages.EXTERNALIZE_STRINGS_DIALOG_TITLE).setFocus();
		bot.shell(VpeUIMessages.EXTERNALIZE_STRINGS_DIALOG_TITLE).activate();
		/*
		 * Check that the property value text is empty
		 */
		SWTBotText defValueText = bot.textWithLabelInGroup(
				VpeUIMessages.EXTERNALIZE_STRINGS_DIALOG_PROPERTIES_VALUE,
				VpeUIMessages.EXTERNALIZE_STRINGS_DIALOG_PROPS_STRINGS_GROUP);
		assertNotNull(CANNOT_FIND_PROPERTY_VALUE, defValueText);
		assertText("", defValueText); //$NON-NLS-1$
		/*
		 * Close the dialog
		 */
		bot.shell(VpeUIMessages.EXTERNALIZE_STRINGS_DIALOG_TITLE).close();
		/*
		 * Type some text outside the tag
		 */
		editor.toTextEditor().typeText(13, 0, COMPLEX_TEXT);
		/*
		 * Select nothing and call the dialog --
		 * the whole text should be selected.
		 */
		editor.toTextEditor().selectRange(13, 3, 0);
		/*
		 * Activate the dialog
		 */
		bot.toolbarButtonWithTooltip(VpeUIMessages.EXTERNALIZE_STRINGS).click();
		bot.shell(VpeUIMessages.EXTERNALIZE_STRINGS_DIALOG_TITLE).setFocus();
		bot.shell(VpeUIMessages.EXTERNALIZE_STRINGS_DIALOG_TITLE).activate();
		/*
		 * Check that the property key and value text
		 */
		SWTBotText defKeyText = bot.textWithLabelInGroup(
				VpeUIMessages.EXTERNALIZE_STRINGS_DIALOG_PROPERTIES_KEY,
				VpeUIMessages.EXTERNALIZE_STRINGS_DIALOG_PROPS_STRINGS_GROUP);
		assertNotNull(CANNOT_FIND_PROPERTY_VALUE, defKeyText);
		assertText(COMPLEX_KEY_RESULT, defKeyText);
		defValueText = bot.textWithLabelInGroup(
				VpeUIMessages.EXTERNALIZE_STRINGS_DIALOG_PROPERTIES_VALUE,
				VpeUIMessages.EXTERNALIZE_STRINGS_DIALOG_PROPS_STRINGS_GROUP);
		assertNotNull(CANNOT_FIND_PROPERTY_VALUE, defValueText);
		assertText(COMPLEX_VALUE_RESULT, defValueText);
		/*
		 * Close the dialog
		 */
		bot.shell(VpeUIMessages.EXTERNALIZE_STRINGS_DIALOG_TITLE).close();
		/*
		 * Check selection in the attribute's value
		 */
		editor.toTextEditor().selectRange(22, 50, 0);
		/*
		 * Activate the dialog
		 */
		bot.toolbarButtonWithTooltip(VpeUIMessages.EXTERNALIZE_STRINGS).click();
		bot.shell(VpeUIMessages.EXTERNALIZE_STRINGS_DIALOG_TITLE).setFocus();
		bot.shell(VpeUIMessages.EXTERNALIZE_STRINGS_DIALOG_TITLE).activate();
		/*
		 * Check that the property value text is empty
		 */
		defValueText = bot.textWithLabelInGroup(
				VpeUIMessages.EXTERNALIZE_STRINGS_DIALOG_PROPERTIES_VALUE,
				VpeUIMessages.EXTERNALIZE_STRINGS_DIALOG_PROPS_STRINGS_GROUP);
		assertNotNull(CANNOT_FIND_PROPERTY_VALUE, defValueText);
		assertText("true", defValueText); //$NON-NLS-1$
		/*
		 * Close the dialog
		 */
		bot.shell(VpeUIMessages.EXTERNALIZE_STRINGS_DIALOG_TITLE).close();
	}
}