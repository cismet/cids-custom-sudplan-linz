/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.sudplan.linz;

import Sirius.server.middleware.types.MetaClass;
import Sirius.server.middleware.types.MetaObject;

import org.openide.util.lookup.ServiceProvider;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import de.cismet.cids.custom.objectactions.sudplan.ActionProviderFactory;
import de.cismet.cids.custom.sudplan.linz.wizard.EtaWizardAction;
import de.cismet.cids.custom.sudplan.linz.wizard.SwmmPlusEtaWizardAction;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cids.utils.interfaces.CidsBeanAction;

import de.cismet.ext.CExtContext;
import de.cismet.ext.CExtProvider;

/**
 * DOCUMENT ME!
 *
 * @author   pascal.dihe@cismet.de
 * @version  1.0, 2012/08/29
 */
@ServiceProvider(service = CExtProvider.class)
public final class SwmmActionCExtProvider implements CExtProvider<CidsBeanAction> {

    //~ Instance fields --------------------------------------------------------

    private final String ifaceClass;
    private final String etaWizardActionClass;
    private final String swmmWizardActionClass;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new RainfallActionCExtProvider object.
     */
    public SwmmActionCExtProvider() {
        ifaceClass = "de.cismet.cids.utils.interfaces.CidsBeanAction";                                     // NOI18N
        etaWizardActionClass = "de.cismet.cids.custom.sudplan.local.linz.wizard.EtaWizardAction";          // NOI18N
        swmmWizardActionClass = "de.cismet.cids.custom.sudplan.local.linz.wizard.SwmmPlusEtaWizardAction"; // NOI18N
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public Collection<? extends CidsBeanAction> provideExtensions(final CExtContext context) {
        final List<CidsBeanAction> actions = new ArrayList<CidsBeanAction>(2);

        if (context != null) {
            final Object ctxReference = context.getProperty(CExtContext.CTX_REFERENCE);

            final Object ctxObject;
            if (ctxReference instanceof Collection) {
                final Collection ctxCollection = (Collection)ctxReference;

                if (ctxCollection.size() == 1) {
                    ctxObject = ctxCollection.iterator().next();
                } else {
                    ctxObject = null;
                }
            } else if (ctxReference instanceof Object[]) {
                final Object[] ctxArray = (Object[])ctxReference;

                if (ctxArray.length == 1) {
                    ctxObject = ctxArray[0];
                } else {
                    ctxObject = null;
                }
            } else {
                ctxObject = ctxReference;
            }

            final MetaClass mc;
            final CidsBean ctxBean;
            if (ctxObject instanceof CidsBean) {
                ctxBean = (CidsBean)ctxObject;
                mc = ctxBean.getMetaObject().getMetaClass();
            } else if (ctxObject instanceof MetaObject) {
                final MetaObject mo = (MetaObject)ctxObject;
                ctxBean = mo.getBean();
                mc = mo.getMetaClass();
            } else {
                ctxBean = null;
                mc = null;
            }

            if (((mc != null) && (ctxBean != null))
                        && SwmmPlusEtaWizardAction.TABLENAME_SWMM_PROJECT.equalsIgnoreCase(mc.getTableName())) {
                final CidsBeanAction etaWizardAction = ActionProviderFactory.getCidsBeanAction(EtaWizardAction.class);
                final CidsBeanAction swmmWizardAction = ActionProviderFactory.getCidsBeanAction(
                        SwmmPlusEtaWizardAction.class);

                etaWizardAction.setCidsBean(ctxBean);
                swmmWizardAction.setCidsBean(ctxBean);

                actions.add(etaWizardAction);
                actions.add(swmmWizardAction);
            }
        }

        return actions;
    }

    @Override
    public Class<? extends CidsBeanAction> getType() {
        return CidsBeanAction.class;
    }

    @Override
    public boolean canProvide(final Class<?> c) {
        final String cName = c.getCanonicalName();

        return (cName == null)
            ? false
            : (ifaceClass.equals(cName)
                        || etaWizardActionClass.equals(cName)
                        || swmmWizardActionClass.equals(cName));
    }
}
