package org.nuxeo.snapshot.bean;

import java.io.Serializable;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.platform.ui.web.api.NavigationContext;
import org.nuxeo.ecm.webapp.helpers.EventManager;
import org.nuxeo.ecm.webapp.versioning.VersionedActions;
import org.nuxeo.snapshot.Snapshot;

@Name("vFolderActions")
@Scope(ScopeType.PAGE)
public class VFolderActionBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @In(create = true)
    protected VersionedActions versionedActions;

    @In(create = true)
    protected transient NavigationContext navigationContext;

    @In(create = true, required = false)
    protected transient CoreSession documentManager;

    public String restoreToVersion() throws ClientException {
        String vuuid = versionedActions.getSelectedVersionId();
        if (vuuid != null) {

            DocumentModel vfolder = documentManager.getDocument(new IdRef(vuuid));
            DocumentModel livefolder = documentManager.getDocument(new IdRef(vfolder.getVersionSeriesId()));

            Snapshot snap = livefolder.getAdapter(Snapshot.class);
            DocumentModel restoredFolder = snap.restore(vfolder.getVersionLabel());

            documentManager.save();

            EventManager.raiseEventsOnDocumentChange(restoredFolder);
            return navigationContext.navigateToDocument(restoredFolder, "after-edit");
        }
        return null;
    }

}
