import com.zhuofan.core.datasource.JdbcDao;
import com.zhuofan.core.common.CommonFunc;
import com.zhuofan.core.common.CommonObject;
import java.util.HashMap;
import java.util.LinkedHashMap;
import com.zhuofan.core.eval.Eval;
import com.zhuofan.core.orm.model.*;
import com.zhuofan.core.orm.dao.*;
public class C_387253aa7fce50267a77564eb4300974
{
    public Object getEvalResult(HashMap mapRow) throws Exception
    {
        return mapRow.get("NOTICE_CONTENT").toString().isEmpty()?"":"<span datavalue='"+mapRow.get("NOTICE_CONTENT")+"' onclick='pw_notify_deadnumline_view(this);'>查看</span>";

    }
}
