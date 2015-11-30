/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package grandroid.phone;

import grandroid.database.Table;

/**
 *
 * @author Rovers
 */
@Table("Contact")
public class Contact {

    /**
     *
     */
    protected Integer _id;
    /**
     *
     */
    protected String name;
    /**
     *
     */
    protected String email;

    protected String number;

    /**
     *
     */
    protected boolean member;
    /**
     *
     */
    protected boolean selected;

    /**
     *
     * @return
     */
    public Integer get_id() {
        return _id;
    }

    /**
     *
     * @param _id
     */
    public void set_id(Integer _id) {
        this._id = _id;
    }

    /**
     *
     * @return
     */
    public String getEmail() {
        return email;
    }

    /**
     *
     * @param email
     */
    public void setEmail(String email) {
        this.email = email;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    /**
     *
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     *
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     *
     * @return
     */
    public boolean isMember() {
        return member;
    }

    /**
     *
     * @param member
     */
    public void setMember(boolean member) {
        this.member = member;
    }

    /**
     *
     * @return
     */
    public boolean isSelected() {
        return selected;
    }

    /**
     *
     * @param selected
     */
    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
