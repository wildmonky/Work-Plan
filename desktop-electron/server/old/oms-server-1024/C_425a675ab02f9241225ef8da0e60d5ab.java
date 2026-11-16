import com.zhuofan.core.datasource.JdbcDao;
import com.zhuofan.core.common.CommonFunc;
import com.zhuofan.core.common.CommonObject;
import java.util.HashMap;
import java.util.LinkedHashMap;
import com.zhuofan.core.eval.Eval;
import com.zhuofan.core.orm.model.*;
import com.zhuofan.core.orm.dao.*;
public class C_425a675ab02f9241225ef8da0e60d5ab
{
    public Object getEvalResult(HashMap mapRow) throws Exception
    {
        return mapRow.get("NOTIFY_NUMBER").toString().isEmpty()?"":"<span datavalue='"+mapRow.get("NOTIFY_NUMBER")+"' onclick='pw_notify_deadnumline_view(this);'>查看</span>";

    }
}
