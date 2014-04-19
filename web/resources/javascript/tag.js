/* 
 * Copyright (C) 2014 maartenl
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
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

if (!yppo)
    var yppo = {}
if (!yppo.tags)
{
    yppo.tags =
            {                
            }
}

/**
 * Returns the tags of a photograph, in the form of an array of tags.
 * @param Long id id of a photograph
 * @param callBack function called when completed.
 * @returns array of tags
 */
yppo.tags.getTags = function(id, callBack)
{
    log.debug("getTags");
    var url = '/YourPersonalPhotographOrganiser/resources/photographs/' + id + '/tags';
    $.get(url,
            function(data) {
                log.debug(data);
                if (data === null)
                {
                    return;
                }
                callBack(data);
            }, // end function(data)
            "json"); // endget
    // url [, data] [, success(data, textStatus, jqXHR)] [, dataType] )
}


/**
 * Returns all tags, in the form of an array of tags.
 * @param callBack function called when completed.
 * @returns array of tags
 */
yppo.tags.getAllTags = function(callBack)
{
    log.debug("getAllTags");
    var url = '/YourPersonalPhotographOrganiser/resources/tags';
    $.get(url,
            function(data) {
                log.debug(data);
                if (data === null)
                {
                    return;
                }
                callBack(data);
            }, // end function(data)
            "json"); // endget
    // url [, data] [, success(data, textStatus, jqXHR)] [, dataType] )
}

/**
 * Returns a summary, in the form of an array of tags, containing the text
 * and the amount of time it was used.
 * @param callBack function called when completed.
 * @returns array of Objects with "text" and "weight" values.
 */
yppo.tags.getSummary = function(callBack)
{
    log.debug("getSummary");
    var url = '/YourPersonalPhotographOrganiser/resources/tags/summary';
    $.get(url,
            function(data) {
                log.debug(data);
                if (data === null)
                {
                    return;
                }
                callBack(data);
            }, // end function(data)
            "json"); // endget
    // url [, data] [, success(data, textStatus, jqXHR)] [, dataType] )
}

/**
 * Returns all gallery photographs that have been tagged with tagname.
 * @param tagname the tag
 * @param callBack function called when completed.
 * @returns array of galleryPhotographs
 */
yppo.tags.getPhotographsFromTag = function(tagname, callBack)
{
    log.debug("getPhotographsFromTag");
    var url = '/YourPersonalPhotographOrganiser/resources/tags/photographs/' + tagname;
    $.get(url,
            function(data) {
                log.debug(data);
                if (data === null)
                {
                    return;
                }
                callBack(data);
            }, // end function(data)
            "json"); // endget
    // url [, data] [, success(data, textStatus, jqXHR)] [, dataType] )
}