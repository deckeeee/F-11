/*
 * =============================================================================
 * Projrct F-11 - Web SCADA for Java
 * Copyright (C) 2002 Freedom, Inc. All Rights Reserved.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *
 */

package org.F11.scada.server.comment;

import org.F11.scada.util.RmiUtil;

/**
 * ポイントコメントサービス実装クラスです。クライアントから要求された、ポイントコメントをDBに格納します。
 * @author maekawa
 *
 */
public class PointCommentServiceImpl implements PointCommentService {
	private PointCommentDao dao;

	public PointCommentServiceImpl() {
		RmiUtil.registryServer(this, PointCommentService.class);
	}

	public void setPointCommentDao(PointCommentDao dao) {
		this.dao = dao;
	}
	
	public PointCommentDto getPointCommentDto(PointCommentDto dto) {
		return dao.select(dto);
	}

	public void setPointComment(PointCommentDto dto) {
		PointCommentDto oldData = dao.select(dto);
		if (oldData == null) {
			dao.insert(dto);
		} else {
			oldData.setComment(dto.getComment());
			dao.update(oldData);
		}
	}
}
