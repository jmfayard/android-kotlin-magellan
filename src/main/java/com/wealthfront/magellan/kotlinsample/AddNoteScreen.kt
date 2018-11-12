package com.wealthfront.magellan.kotlinsample

import android.content.Context
import com.marcinmoskala.kotlinandroidviewbindings.bindToClick
import com.marcinmoskala.kotlinandroidviewbindings.bindToEditText
import com.marcinmoskala.kotlinandroidviewbindings.bindToRequestFocus
import com.wealthfront.magellan.IDisplay
import com.wealthfront.magellan.MagellanScreen
import com.wealthfront.magellan.ScreenSetups
import com.wealthfront.magellan.UiCallback
import com.wealthfront.magellan.UseCoroutines
import com.wealthfront.magellan.hideSoftKeyboard
import kotlinx.coroutines.launch
import java.util.UUID

interface AddNote : IDisplay {
    var onSubmit: UiCallback
    var title: String
    var description: String
    val focus: UiCallback
    fun focus() = focus.invoke()
}

class AddNoteScreen(
    val noteId: String? = null,
    val repository: NotesRepository = InMemoryRepository
) : UseCoroutines,
    MagellanScreen<AddNote>(
        screenLayout = R.layout.addnote_screen,
        screenTitle = if (noteId != null) R.string.title_editnote else R.string.title_addnote,
        screenSetup = ScreenSetups::displayAddNote
    ) {

    val isEditMode = noteId != null

    override fun onShow(context: Context) {
        super.onShow(context)
        if (isEditMode) {
            showExistingNote()
        }
        display?.focus()
        display?.onSubmit = this::onSubmit
    }

    fun onSubmit() {
        launch {
            hideSoftKeyboard(R.id.add_note_title)
            repository.saveNote(updatedNote())
            navigator.goBack()
        }
    }

    fun showExistingNote() {
        launch {
            val note = repository.getNote(noteId!!)
            if (note == null) {
                navigator?.goBack()
            } else {
                display?.title = note.title
                display?.description = note.description
            }
        }
    }

    fun updatedNote(): Note {
        val d = requireNotNull(display)
        val title = d.title
        val description = d.description
        val id = if (isEditMode) noteId!! else UUID.randomUUID().toString()
        return Note(id, description, title)
    }
}

fun ScreenSetups.displayAddNote() = object : AddNote {

    override var onSubmit: UiCallback by bindToClick(R.id.add_note_save)

    override var title: String by bindToEditText(R.id.add_note_title)

    override var description: String by bindToEditText(R.id.add_note_description)

    override val focus: UiCallback by bindToRequestFocus(R.id.add_note_title)
}
