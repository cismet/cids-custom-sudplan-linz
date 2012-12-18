/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.linz.objectactions;

import java.util.Arrays;
import java.util.Collection;

import de.cismet.cids.custom.objectactions.sudplan.ActionProviderFactory;
import de.cismet.cids.custom.sudplan.linz.wizard.EtaWizardAction;
import de.cismet.cids.custom.sudplan.linz.wizard.SwmmPlusEtaWizardAction;
import de.cismet.cids.custom.sudplan.linz.wizard.UploadWizardAction;

import de.cismet.cids.utils.interfaces.CidsBeanAction;
import de.cismet.cids.utils.interfaces.CidsBeanActionsProvider;

/**
 * DOCUMENT ME!
 *
 * @author   pascal.dihe@cismet.de
 * @version  $Revision$, $Date$
 */
public final class SwmmProjectActionsProvider implements CidsBeanActionsProvider {

    //~ Methods ----------------------------------------------------------------

    @Override
    public Collection<CidsBeanAction> getActions() {
        return Arrays.asList(
                new CidsBeanAction[] {
                    ActionProviderFactory.getCidsBeanAction(EtaWizardAction.class),
                    ActionProviderFactory.getCidsBeanAction(SwmmPlusEtaWizardAction.class),
                    ActionProviderFactory.getCidsBeanAction(UploadWizardAction.class)
                });
    }
}
