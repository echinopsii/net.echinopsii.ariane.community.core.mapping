define(
    [
        'taitale-helper',
        'taitale-dictionaries',
        'taitale-btree',
        'taitale-otree'
    ],
    function (helper_, dictionaries, btree, otree) {

        function TreeGroups() {
            this.helper = new helper_();
            this.groups = []
        }

        var dic = new dictionaries();
        var propagateTreeColor = function(root, group) {
            var i, ii, object;
            for (i=0, ii=root.linkedTreeObjects.length; i < ii; i++) {
                object = root.linkedTreeObjects[i];
                if (! object.hasOwnProperty('treeColor') || object.treeColor == null) {
                    object.treeColor = root.treeColor;
                    group.push(object);
                    propagateTreeColor(object, group);
                }
            }
        };

        TreeGroups.prototype.computeTreeGroups = function(objects, layout) {
            var i, ii, group, object, lTree;
            this.groups = [];
            for (i=0, ii=objects.length; i<ii; i++) {
                object = objects[i];
                if (!object.hasOwnProperty('treeColor') || object.treeColor == null) {
                    object.treeColor = 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function(c) {
                        var r = Math.random()*16|0, v = c == 'x' ? r : (r&0x3|0x8);
                        return v.toString(16);
                    });
                    group = [];
                    group.push(object);
                    propagateTreeColor(object, group);

                    if (layout===dic.mapLayout.BBTREE) lTree = new btree();
                    else if (layout===dic.mapLayout.OBTREE) lTree = new otree();

                    this.groups.push({
                        gTree: group,
                        lTree: lTree
                    });
                }
            }
            // this.helper.debug("[TreeGroups.computeTreeGroups] groups : " + this.groups.length)
        };

        TreeGroups.prototype.sort = function(fc) {
            var i, ii;
            for (i=0, ii=this.groups.length; i < ii; i++)
                this.groups[i].gTree.sort(fc);
        };

        TreeGroups.prototype.loadTrees = function() {
            var i, ii, tree, group;
            for (i=0, ii=this.groups.length; i < ii; i++) {
                tree = this.groups[i].lTree;
                group = this.groups[i].gTree;
                tree.loadTree(group[0]);
            }
        };

        TreeGroups.prototype.reloadTrees = function() {
            var i, ii, tree, group;
            for (i=0, ii=this.groups.length; i < ii; i++) {
                tree = this.groups[i].lTree;
                group = this.groups[i].gTree;
                tree.reloadTree(group[0]);
            }
        };

        TreeGroups.prototype.definePoz = function() {
            var i, ii, j, jj, tree, group;
            var squareLen = Math.round(Math.sqrt(this.groups.length)) + 1, line = 0, column = 0, shiftX = 0, shiftY = 0;
            var maxLineHeight = [], maxColumnWidth = [];

            for (i=0, ii=this.groups.length; i < ii; i++) {
                line = Math.round(i / squareLen);
                column = i % squareLen;

                tree = this.groups[i].lTree;
                group = this.groups[i].gTree;
                for (j=0, jj=column; j<jj; j++) shiftX += maxColumnWidth[j]*1.2;
                for (j=0, jj=line; j<jj; j++) shiftY += maxLineHeight[j]*1.2;

                tree.definePoz(shiftX, shiftY);

                if (maxLineHeight.length > line) {
                    if (maxLineHeight[line] < tree.treeHeight)
                        maxLineHeight[line] = tree.treeHeight;
                } else maxLineHeight[line] = tree.treeHeight;

                if (maxColumnWidth.length > column) {
                    if (maxColumnWidth[column] < tree.treeWidth)
                        maxColumnWidth[column] = tree.treeWidth;
                } else maxColumnWidth[column] = tree.treeWidth;
            }
        };

        return TreeGroups;
    });