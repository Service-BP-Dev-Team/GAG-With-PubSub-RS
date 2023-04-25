/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package smartworkflow.dwfms.lifa.miu.view.util.pagination;

import java.util.ArrayList;

/**
 *
 * @author Ndadji Maxime
 */
public interface PaginationObserver<T> {
    public void update(ArrayList<T> data);
}
