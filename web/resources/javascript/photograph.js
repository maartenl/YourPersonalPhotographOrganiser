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
if (!yppo.photographs)
{
    yppo.photographs =
            {                
            }
}

/**
 * Returns the photographs of a gallery, in the form of an array of photographs.
 * @param Long id id of a gallery
 * @param callBack function called when completed.
 * @returns array of galleryPhotographs
 */
yppo.photographs.getPhotographs = function(id, callBack)
{
    log.debug("getPhotographs");
    var url = '/YourPersonalPhotographOrganiser/resources/galleries/' + id + '/photographs';
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
 * Returns the metadata of a photograph.
 * @param Long id id of a photograph
 * @param callBack function called when completed.
 * @returns extensive array of photograph metadata
 */
yppo.photographs.getMetadata = function(id, callBack)
{
    log.debug("getMetadata");
    var url = '/YourPersonalPhotographOrganiser/resources/photographs/' + id + '/metadata';
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

