package de.fayard.android.kiss.notes

import android.content.Context
import com.marcinmoskala.kotlinandroidviewbindings.*
import de.fayard.android.kiss.*
import de.fayard.android.kiss.R
import kotlinx.coroutines.launch

interface NoteDetail : IDisplay {
    var title: String
    var description: String
    var onEditNote: UiCallback
}

class NoteDetailScreen(
    val noteId: String,
    val repository: NotesRepository = InMemoryRepository
) : UseCoroutines, HasSnackbar, MagellanScreen<NoteDetail>(
    R.layout.notedetail_screen, R.string.title_notedetails, ScreenSetups::displayNoteDetail
) {

    override fun onShow(context: Context) {
        super.onShow(context)
        display?.onEditNote = this::onEditNote
        fetchAndDisplayNote()
    }

    fun fetchAndDisplayNote() = launch {
        val note = repository.getNote(noteId)
        if (note == null) {
            snackbar("ERROR, cannot find note $noteId")
            navigator.goBack()
        } else {
            display?.title = note.title
            display?.description = "${note.description}\n🔗 ${note.url}"
        }
    }

    fun onEditNote() = navigator.goTo(AddNoteScreen(noteId))
}

fun ScreenSetups.displayNoteDetail() = object : NoteDetail {
    override var title: String by bindToText(R.id.note_detail_title)

    override var description: String by bindToText(R.id.note_detail_description)

    override var onEditNote: UiCallback by bindToClick(R.id.note_detail_edit)
}
